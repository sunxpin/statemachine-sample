package com.baoxian.common.statemachine;

import com.baoxian.common.annotation.StateMachineAction;
import com.baoxian.common.dao.BaseDao;
import com.baoxian.common.services.BaseService;
import com.baoxian.common.statemachine.entity.Timer;
import com.baoxian.common.statemachine.entity.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.config.configurers.InternalTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.support.DefaultExtendedState;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseStatemachineService<T extends StateMachineEntity<?>> extends BaseService<T> {

    private static Logger logger = LoggerFactory.getLogger(BaseStatemachineService.class);

    @Autowired
    private RedissonClient redisson;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private StateService stateService;
    @Autowired
    private EventService eventService;
    @Autowired
    private StateEventService stateEventService;
    @Autowired
    private TimerService timerService;
    @Autowired
    private LogService logService;

    private static HashMap<String, StateMachineBuilder.Builder<String, String>> builders = new HashMap<>();
    public static HashMap<String, BaseStatemachineService> services = new HashMap<>();

    @Transactional
    public void dispatchEvent(String id, String eventCode, Object params) {
        RRateLimiter rateLimiter = redisson.getRateLimiter(RateLimiterNamespace.STATEMACHINE_EVENT + getServiceEntity() + id + eventCode);
        rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
        if (rateLimiter.tryAcquire()) {
            StateMachine<String, String> acquireStateMachine = this.acquireStateMachine(id);
            boolean success = acquireStateMachine.sendEvent(eventCode);
            Object error = acquireStateMachine.getExtendedState().getVariables().get("error");
            if (!success || !Objects.isNull(error)) {
                System.err.println(error);
                throw new RuntimeException("不能执行");
            }
            Log log = new Log();
            log.setEvent(eventCode);
            log.setDepartment_id("测试部门");
            log.setEntity(getServiceEntity());
            log.setEntity_id(id);
            log.setOperator_id("测试用户");
            if (params != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                String writeValueAsString;
                try {
                    writeValueAsString = objectMapper.writeValueAsString(params);
                    log.setParams(writeValueAsString);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            logService.insert(log);
        }
    }

    abstract protected void init();

    @PostConstruct
    public void init_() {
        this.base_init();
    }

    public void base_init() {
        init();
        try {
            init_statemachine();
        } catch (Exception ex) {
            logger.error("状态机初始化失败,{}", ex);
        }
        services.put(getServiceEntity(), this);
    }

    public void init_statemachine() {
        State state = new State();
        state.setEntity(getServiceEntity());
        Set<State> allStates = stateService.query(state).stream().collect(Collectors.toSet());
        if (allStates.isEmpty()) return;
        try {
            init_statemachine_builder(allStates);
        } catch (Exception ex) {
            logger.error("状态机初始化失败,{}", ex);
        }
        logger.info("状态机初始化完成：{}", getServiceEntity());
    }

    public void init_statemachine_builder(Set<State> states) throws Exception {
        StateMachineBuilder.Builder<String, String> builder = StateMachineBuilder.builder();
        builder.configureConfiguration().withVerifier().enabled(true);
        builder.configureStates().withStates().states(states.stream().map(state -> state.getCode()).collect(Collectors.toSet()));
        for (State state : states) {
            try {
                if (state.getState_type().equals("BEGIN")) {
                    builder.configureStates().withStates().initial(state.getCode());
                } else if (state.getState_type().equals("END")) {
                    builder.configureStates().withStates().end(state.getCode());
                } else if (state.getState_type().equals("CHOICE")) {
                    builder.configureStates().withStates().choice(state.getCode());
                    builder.configureTransitions().withChoice().first(stateService.queryById(state.getFirst_target_id()).getCode(), guardFactory(state.getFirst_guard_spel()));
                    if (state.getThen_target_id() != null) {
                        builder.configureTransitions().withChoice().then(stateService.queryById(state.getThen_target_id()).getCode(), guardFactory(state.getThen_guard_spel()));
                    }
                    builder.configureTransitions().withChoice().last(stateService.queryById(state.getLast_target_id()).getCode());
                }

                if (StringUtils.hasText(state.getEnter_action())) {
                    builder.configureStates()
                            .withStates()
                            .stateEntry(state.getCode(), UnauthorizeActionFactory(state.getEnter_action()), errorAction());
                } else if (StringUtils.hasText(state.getExit_action())) {
                    builder.configureStates()
                            .withStates()
                            .stateExit(state.getCode(), UnauthorizeActionFactory(state.getExit_action()), errorAction());
                }

                Timer t = new Timer();
                t.setSource_state_id(state.getId());
                List<Timer> timers = timerService.query(t);
                for (Timer timer : timers) {
                    if (timer.getTime_interval() != null && !timer.getTime_interval().equals(0)) {
                        if (timer.getTime_interval() < 10) {
                            logger.error("timer间隔不低于10s");
                        }
                    } else if (timer.getTime_once() != null && !timer.getTime_once().equals(0)) {
                        if (timer.getTime_once() < 10) {
                            logger.error("timer间隔不低于10s");
                        }
                    }
                    if (!StringUtils.hasText(timer.getAction())) {
                        logger.error("Timer的动作不能为空");
                    }
                    try {
                        Method method = this.getClass().getDeclaredMethod(timer.getAction(), String.class, StateMachine.class);
                        StateMachineAction annotation = method.getDeclaredAnnotation(StateMachineAction.class);
                        if (annotation == null) {
                            logger.error(getServiceEntity() + "服务 ," + timer.getAction() + "的方法，必须使用StateMachineAction注解，才能生效");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        logger.error(getServiceEntity() + "服务，没有" + timer.getAction() + "的方法");
                    } catch (SecurityException e) {
                        e.printStackTrace();
                        logger.error(getServiceEntity() + "服务，禁止访问" + timer.getAction() + "方法");
                    }
                }

                StateEvent se = new StateEvent();
                se.setState_id(state.getId());
                List<StateEvent> query = stateEventService.query(se);
                List<String> ids = new ArrayList<>();
                query.stream().forEach(stateEvent -> ids.add(stateEvent.getEvent_id()));
                List<Event> events = eventService.queryByIds(ids);

                for (Event event : events) {
                    if (event.getTarget_id() == null) {
                        InternalTransitionConfigurer<String, String> eventTemp1 = builder.configureTransitions()
                                .withInternal().source(state.getCode()).event(event.getCode());
                        if (StringUtils.hasText(event.getAction())) {
                            eventTemp1.action(AuthorizeActionFactory(state, event.getAction()), errorAction());
                        }
                        if (StringUtils.hasText(event.getGuard_spel())) {
                            eventTemp1.guard(guardFactory(event.getGuard_spel()));
                        }
                    } else {
                        ExternalTransitionConfigurer<String, String> eventTemp2 = builder
                                .configureTransitions()
                                .withExternal()
                                .source(state.getCode())
                                .target(eventService.queryById(event.getTarget_id()).getCode())
                                .event(event.getCode());
                        if (StringUtils.hasText(event.getAction())) {
                            eventTemp2.action(AuthorizeActionFactory(state, event.getAction()), errorAction());
                        }
                        if (StringUtils.hasText(event.getGuard_spel())) {
                            eventTemp2.guard(guardFactory(event.getGuard_spel()));
                        }
                    }
                }
                builders.put(getServiceEntity(), builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public StateMachine<String, String> acquireStateMachine(String id) {
        T findById = queryById(id);
        if (findById != null) {
            StateMachineBuilder.Builder<String, String> builder = builders.get(getServiceEntity());
            if (builder == null) {
                throw new RuntimeException("状态机获取失败");
            }
            StateMachine<String, String> stateMachine = builder.build();
            String stateCode = null;
            if (findById.getState() != null) {
                stateCode = stateService.queryById(findById.getState()).getCode();
            }
            DefaultExtendedState defaultExtendedState = new DefaultExtendedState();
            defaultExtendedState.getVariables().put("id", id);
            defaultExtendedState.getVariables().put("entity", getServiceEntity());
            DefaultStateMachineContext<String, String> stateMachineContext = new DefaultStateMachineContext<>(
                    stateCode, null, null, defaultExtendedState, null, null);

            stateMachine
                    .getStateMachineAccessor()
                    .doWithRegion(function -> {
                        //状态初始化
                        function.resetStateMachine(stateMachineContext);
                        //处理状态机状态变化的持久化和定时器方法
                        function.addStateMachineInterceptor(new StateMachineInterceptorAdapter<String, String>() {
                            @Override
                            public void postStateChange(org.springframework.statemachine.state.State<String, String> state,
                                                        Message<String> message,
                                                        Transition<String, String> transition,
                                                        StateMachine<String, String> stateMachine1) {
                                State object = new State();
                                object.setEntity(getServiceEntity());
                                object.setCode(state.getId());
                                String findByEntityAndCode = stateService.queryOne(object).getCode();

                                if (findByEntityAndCode == null) {
                                    throw new RuntimeException("不存在的状态" + stateMachine1.getState().getId());
                                }

                                findById.setState(findByEntityAndCode);
                                updateById(findById);

                                stopJobs(getServiceEntity(), id);

                                Timer timer = new Timer();
                                timer.setEntity(getServiceEntity());
                                timer.setCode(findByEntityAndCode);
                                List<Timer> timers = timerService.query(timer);

                                if (timers != null && !timers.isEmpty()) {
                                    startJobs(getServiceEntity(), id, timers);
                                }
                            }

                            @Override
                            public StateContext<String, String> preTransition(StateContext<String, String> stateContext) {
                                if (stateContext.getTransition().getGuard() != null) {
                                    boolean evaluate = stateContext.getTransition().getGuard().evaluate(stateContext);
                                    if (!evaluate) {
                                        stateContext.getExtendedState().getVariables().put("error", 1);
                                    }
                                }
                                return stateContext;
                            }
                        });
                    });
            stateMachine.getExtendedState().getVariables().put("id", id);
            stateMachine.getExtendedState().getVariables().put("entity", getServiceEntity());
            stateMachine.start();
            return stateMachine;
        } else {
            throw new RuntimeException("id不存在");
        }
    }

    /**
     * 刷新状态机
     *
     * @return
     */
    public StateMachineBuilder.Builder<String, String> refreshBuilder() {
        State state = new State();
        state.setEntity(getServiceEntity());
        Set<State> states = stateService.query(state).stream().collect(Collectors.toSet());
        try {
            init_statemachine_builder(states);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(getServiceEntity() + "状态机 ,更新失败");
        }
        return builders.get(getServiceEntity());
    }

    private void stopJobs(String serviceEntity, String id) {
        GroupMatcher<JobKey> jobKeyGroupMatcher = GroupMatcher.jobGroupEquals(serviceEntity + id);
        Set<JobKey> jobKeySet;
        try {
            jobKeySet = scheduler.getJobKeys(jobKeyGroupMatcher);
            for (JobKey jobKey : jobKeySet) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                if (jobDetail == null) return;
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    private void startJobs(String serviceEntity, String id, List<Timer> timers) {
        for (Timer timer : timers) {
            JobDataMap map = new JobDataMap();
            map.put("entity", serviceEntity);
            map.put("id", id);
            map.put("action", timer.getAction());

            JobDetail jobDetail = JobBuilder.newJob(QuartzTimerJob.class).withIdentity(timer.getCode(), serviceEntity + id).usingJobData(map).build();
            SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
            Calendar calendar = Calendar.getInstance();
            if (timer.getTime_once() != null && timer.getTime_once() > 0) {
                simpleScheduleBuilder.withIntervalInSeconds(timer.getTime_once());
                simpleScheduleBuilder.withRepeatCount(0);
                calendar.add(Calendar.SECOND, timer.getTime_once());
            } else {
                simpleScheduleBuilder.withIntervalInSeconds(timer.getTime_interval()).repeatForever();
                calendar.add(Calendar.SECOND, timer.getTime_interval());
            }
            Date d = calendar.getTime();
            SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger().withIdentity(timer.getCode(), serviceEntity + id).withSchedule(simpleScheduleBuilder).startAt(d).build();
            try {
                scheduler.scheduleJob(jobDetail, simpleTrigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    //spel内的对象user entity
    private Guard<String, String> guardFactory(String spel) {
        return context -> {
            String id = context.getExtendedState().get("id", String.class);
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext spelContext = new StandardEvaluationContext();

//            com.baoxian.entity.User user = getCurrentUser();
//            T findById = queryById(id);
//            spelContext.setVariable("user", user);
//            spelContext.setVariable("entity", findById);

            spelContext.setVariable("user", "12");
            spelContext.setVariable("entity", "99");

            return parser.parseExpression(spel, new TemplateParserContext()).getValue(spelContext, Boolean.class);
        };
    }


    private Action<String, String> errorAction() {
        return context -> {
            Exception exception = context.getException();
            exception.printStackTrace();
            logger.error("action error :{}", exception);
        };
    }

    private Action<String, String> AuthorizeActionFactory(State state, String actionStr) {
        BaseStatemachineService<T> _this = this;
        try {
            Method method = _this.getClass().getDeclaredMethod(actionStr, String.class, StateMachine.class);
            StateMachineAction annotation = method.getDeclaredAnnotation(StateMachineAction.class);
            if (annotation == null) {
                logger.error(getServiceEntity() + "服务 ," + actionStr + "的方法，必须使用StateMachineAction注解，才能生效");
            }
            return context -> {
                Event findByEntityAndCode;
//                    User currentUser = getCurrentUser();
//                    if (currentUser == null) {
//                        findByEntityAndCode = eventRepository.findBySourcesUuidAndActionAndRolesCode(state.getUuid(), actionStr, "user");
//                    } else if (request.getRequestURI().contains("/integration/") || ContextUtils.getRole().equals("developer")) {
//                        findByEntityAndCode = eventRepository.findBySourcesUuidAndAction(state.getUuid(), actionStr);
//                    } else {
//                        findByEntityAndCode = eventRepository.findBySourcesUuidAndActionAndRolesCode(state.getUuid(), actionStr, ContextUtils.getRole());
//                    }
//                    if (findByEntityAndCode == null) {
//                        context.getExtendedState().getVariables().put("error", 1);
//                        logger.error("角色无操作" + actionStr + "权限");
//                    }

                try {
                    method.invoke(_this, context.getExtendedState().get("id", String.class), context.getStateMachine());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    context.getExtendedState().getVariables().put("error", 1);
                    logger.error(getServiceEntity() + "服务 ," + actionStr + "的方法，非法访问");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    context.getExtendedState().getVariables().put("error", 1);
                    logger.error(getServiceEntity() + "服务 ," + actionStr + "的方法，非法参数");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    context.getExtendedState().getVariables().put("error", 1);
                    logger.error("AuthorizeActionFactory exception:{}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            logger.error(getServiceEntity() + "服务，没有" + actionStr + "的方法");
        } catch (SecurityException e) {
            logger.error(getServiceEntity() + "服务，禁止访问" + actionStr + "方法");
        }
        return null;
    }

    private Action<String, String> UnauthorizeActionFactory(String actionStr) {
        BaseStatemachineService<T> _this = this;
        try {
            Method method = _this.getClass().getDeclaredMethod(actionStr, String.class, StateMachine.class);
            StateMachineAction annotation = method.getDeclaredAnnotation(StateMachineAction.class);
            if (annotation == null) {
                logger.error(getServiceEntity() + "服务 ," + actionStr + "的方法，必须使用StateMachineAction注解，才能生效");
            }
            return context -> {
                try {
                    method.invoke(_this, context.getExtendedState().get("id", Long.class), context.getStateMachine());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    context.getExtendedState().getVariables().put("error", 1);
                    logger.error(getServiceEntity() + "服务 ," + actionStr + "的方法，非法访问");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    context.getExtendedState().getVariables().put("error", 1);
                    logger.error(getServiceEntity() + "服务 ," + actionStr + "的方法，非法参数");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    context.getExtendedState().getVariables().put("error", 1);
                    logger.error("UnauthorizeActionFactory error :{}", e);
                }
            };
        } catch (NoSuchMethodException e) {
            logger.error(getServiceEntity() + "服务，没有" + actionStr + "的方法");
        } catch (SecurityException e) {
            logger.error(getServiceEntity() + "服务，禁止访问" + actionStr + "方法");
        }
        return context -> {};
    }

    /**
     * 返回当前实体
     *
     * @return
     */
    public String getServiceEntity() {
        ResolvableType resolvableType = ResolvableType.forClass(super.baseDao.getClass());
        Class<?> entityClass = resolvableType.as(BaseDao.class).getGeneric(0).resolve();
        return entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1);
    }

    /**
     * 实体类型判断判断
     *
     * @param clz
     * @return
     */
    public boolean entityInstanceOf(Class<?> clz) {
        ResolvableType resolvableType = ResolvableType.forClass(super.baseDao.getClass());
        Class<?> entityClass = resolvableType.as(BaseDao.class).getGeneric(0).resolve();
        return clz.isAssignableFrom(entityClass);
    }

    public enum RateLimiterNamespace {
        SMS, LOGIN, FRESH_TOKEN, STATEMACHINE_EVENT
    }
}
