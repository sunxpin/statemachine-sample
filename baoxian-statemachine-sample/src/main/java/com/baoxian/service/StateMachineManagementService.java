package com.baoxian.service;

import com.baoxian.common.plugin.SqlBuilder;
import com.baoxian.common.statemachine.BaseStatemachineService;
import com.baoxian.common.statemachine.EventService;
import com.baoxian.common.statemachine.StateService;
import com.baoxian.common.statemachine.TimerService;
import com.baoxian.common.statemachine.entity.Event;
import com.baoxian.common.statemachine.entity.State;
import com.baoxian.common.statemachine.entity.Timer;
import com.baoxian.controller.StateMachineController;
import com.baoxian.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StateMachineManagementService {

    final static Logger logger = LoggerFactory.getLogger(StateMachineManagementService.class);

    @Autowired
    private StateService stateService;
    @Autowired
    private EventService eventService;
    @Autowired
    private TimerService timerService;

    @Transactional
    public String addState(StateMachineController.AddStateRequest body) {

        State state = new State();
       /* if (!SmartQuery.getNametostructure().containsKey(body.getEntity())) {
            throw new BusinessException(2711, "输入的entity不合法");
        }*/
        state.setEntity(body.getEntity());
        state.setCode(body.getCode());
        state.setDescription(body.getDescription());
        state.setName(body.getName());
        State.StateType stateType = body.getStateType();

        if (stateType.equals(State.StateType.BEGIN)) {
            State state1 = new State();
            state1.setEntity(body.getEntity());
            state1.setState_type("BEGIN");
            State stateEntity = stateService.queryOne(state1);
            if (stateEntity != null) {
                throw new BusinessException(1344, body.getEntity() + "的启动状态已经存在");
            }
        }
        state.setState_type(stateType.name());
        state.setSort(body.getSort());
        String firstTarget = body.getFirstTarget();
        if (firstTarget != null) {
            State s1 = new State();
            s1.setFirst_target_id(firstTarget);
            State queryOne = stateService.queryOne(s1);
            if (queryOne == null) {
                throw new BusinessException(2312, "state id不存在");
            }
            state.setFirst_target_id(firstTarget);
            String firstGuardSpel = body.getFirstGuardSpel();
            if (StringUtils.isEmpty(firstGuardSpel)) {
                throw new BusinessException(2352, "first条件判断不存在");
            }
            state.setFirst_guard_spel(body.getFirstGuardSpel());
            if (!body.getStateType().equals(State.StateType.CHOICE)) {
                throw new BusinessException(2864, "状态类型选择错误");
            }

            String lastTarget = body.getLastTarget();
            if (lastTarget == null) {
                throw new BusinessException(2361, "last Target必须设置");
            }
            State queryById = stateService.queryById(lastTarget);
            if (queryById == null) {
                throw new BusinessException(2313, "state id不存在");
            }
            state.setLast_target_id(lastTarget);

            String thenTarget = body.getThenTarget();
            if (thenTarget != null) {
                State byId = stateService.queryById(thenTarget);
                if (byId == null) {
                    throw new BusinessException(2319, "state id不存在");
                }
                state.setThen_target_id(thenTarget);
                String thenGuardSpel = body.getThenGuardSpel();
                if (StringUtils.isEmpty(thenGuardSpel)) {
                    throw new BusinessException(2351, "then条件判断不存在");
                }
                state.setThen_guard_spel(body.getThenGuardSpel());
            }
            if (!StringUtils.isEmpty(body.getEnterAction())) {
                state.setEnter_action(body.getEnterAction());
            }
            if (!StringUtils.isEmpty(body.getExitAction())) {
                state.setExit_action(body.getExitAction());
            }
        }
        stateService.updateById(state);
        return state.getId();
    }

    public Object deleteState(StateMachineController.DeleteStateRequest body) {
        try {
            stateService.deleteById(body.getId());
        } catch (RuntimeException ex) {
            throw new BusinessException(1371, "请先删除目标是自己的事件关联和定时器");
        }
        return body.getId();
    }

    @Transactional
    public String updateState(StateMachineController.UpdateStateRequest body) {
        State state = stateService.queryById(body.getId());
        if (state == null) {
            throw new BusinessException(2467, "状态id不存在 ");
        }
        /*if (!SmartQuery.getNametostructure().containsKey(body.getEntity())) {
            throw new BusinessException(2711, "输入的entity不合法");
        }*/
        state.setEntity(body.getEntity());
        state.setCode(body.getCode());
        state.setDescription(body.getDescription());
        state.setName(body.getName());
        State.StateType stateType = body.getStateType();

        State exmaple = new State();
        exmaple.setEntity(body.getEntity());
        exmaple.setState_type("BEGIN");
        State stateEntity = stateService.queryOne(exmaple);
        if (stateEntity != null && !stateEntity.getId().equals(body.getId())) {
            throw new BusinessException(1344, body.getEntity() + "的启动状态已经存在");
        }

        state.setState_type(body.getStateType().toString());
        state.setSort(body.getSort());
        String firstTarget = body.getFirstTarget();
        if (firstTarget != null) {
            State first = stateService.queryById(firstTarget);
            if (first == null) {
                throw new BusinessException(2312, "state id不存在");
            }
            state.setFirst_target_id(firstTarget);
            String firstGuardSpel = body.getFirstGuardSpel();
            if (StringUtils.isEmpty(firstGuardSpel)) {
                throw new BusinessException(2352, "first条件判断不存在");
            }
            state.setFirst_guard_spel(body.getFirstGuardSpel());

            if (!body.getStateType().equals(State.StateType.CHOICE)) {
                throw new BusinessException(2864, "状态类型选择错误");
            }

            String lastTarget = body.getLastTarget();
            if (lastTarget == null) {
                throw new BusinessException(2361, "last Target必须设置");
            }
            State last = stateService.queryById(lastTarget);
            if (last == null) {
                throw new BusinessException(2313, "state id不存在");
            }
            state.setLast_target_id(lastTarget);

            String thenTarget = body.getThenTarget();
            if (thenTarget != null) {
                State then = stateService.queryById(thenTarget);
                if (then == null) {
                    throw new BusinessException(2319, "state id不存在");
                }
                state.setThen_target_id(thenTarget);
                String thenGuardSpel = body.getThenGuardSpel();
                if (StringUtils.isEmpty(thenGuardSpel)) {
                    throw new BusinessException(2351, "then条件判断不存在");
                }
                state.setThen_guard_spel(body.getThenGuardSpel());
            }
            if (!StringUtils.isEmpty(body.getEnterAction())) {
                state.setEnter_action(body.getEnterAction());
            }
            if (!StringUtils.isEmpty(body.getExitAction())) {
                state.setExit_action(body.getExitAction());
            }
        }
        stateService.updateById(state);
        return state.getId();
    }

    @Transactional
    public void updateCache(String entity) {
        BaseStatemachineService stateMachineService = BaseStatemachineService.services.get(entity);
        if (stateMachineService != null) {
            stateMachineService.refreshBuilder();
        } else {
            throw new BusinessException(1741, "实体不存在");
        }
    }

    @Transactional
    public String addEvent(StateMachineController.AddEventRequest body) {

        Event event = new Event();
       /* if (!SmartQuery.getNametostructure().containsKey(body.getEntity())) {
            throw new BusinessException(2711, "输入的entity不合法");
        }*/
        event.setEntity(body.getEntity());
        event.setCode(body.getCode());
        event.setDescription(body.getDescription());
        event.setName(body.getName());
        event.setTerminal(body.getTerminal());
        event.setSort(body.getSort());
        if (!StringUtils.isEmpty(body.getAction())) {
            event.setAction(body.getAction());
        }
        String target = body.getTarget();
        if (target != null) {
            State queryById = stateService.queryById(target);
            if (queryById == null) {
                throw new BusinessException(2312, "state id不存在");
            }
            event.setTarget_id(target);
            if (!StringUtils.isEmpty(body.getGuardSpel())) {
                event.setGuard_spel(body.getGuardSpel());
            }
        }
       /* Set<Long> roles = body.getRoles();
        if(roles!=null) {
            HashSet<Role> hashSet = new HashSet<Role>();
            for (Long role : roles) {
                Optional<Role> findById = roleRepository.findById(role);
                if(findById.isPresent()) {
                    hashSet.add(findById.get());
                }
            }
            event.setRoles(hashSet);
        }*/
        eventService.insert(event);
        return event.getId();
    }

    @Transactional
    public String deleteEvent(StateMachineController.DeleteEventRequest body) {
        try {
            eventService.deleteById(body.getId());
        } catch (RuntimeException ex) {
            throw new BusinessException(1371, "请先删除状态下的事件关联和事件的角色关联");
        }
        return body.getId();
    }

    @Transactional
    public String updateEvent(StateMachineController.UpdateEventRequest body) {
        Event event = eventService.queryById(body.getId());
        if (event == null) {
            throw new BusinessException(2467, "事件id不存在 ");
        }
//        if (!SmartQuery.getNametostructure().containsKey(body.getEntity())) {
//            throw new BusinessException(2711, "输入的entity不合法");
//        }
        event.setEntity(body.getEntity());
        event.setCode(body.getCode());
        event.setDescription(body.getDescription());
        event.setName(body.getName());
        event.setTerminal(body.getTerminal());
        event.setSort(body.getSort());
        if (!StringUtils.isEmpty(body.getAction())) {
            event.setAction(body.getAction());
        }
        String target = body.getTarget();
        if (target != null) {
            State findById = stateService.queryById(target);
            if (findById == null) {
                throw new BusinessException(2312, "state id不存在");
            }
            event.setTarget_id(target);
            if (!StringUtils.isEmpty(body.getGuardSpel())) {
                event.setGuard_spel(body.getGuardSpel());
            }
        }
       /* Set<Long> roles = body.getRoles();
        if (roles != null) {
            HashSet<Role> hashSet = new HashSet<Role>();
            for (Long role : roles) {
                Optional<Role> findById = roleRepository.findById(role);
                if (findById.isPresent()) {
                    hashSet.add(findById.get());
                }
            }
            event.setRoles(hashSet);
        }*/
        eventService.updateById(event);
        return event.getId();
    }

    @Transactional
    public String stateAddTimer(StateMachineController.StateAddTimerRequest body) {
        Timer timer = new Timer();
      /*  if (!SmartQuery.getNametostructure().containsKey(body.getEntity())) {
            throw new BusinessException(2711, "输入的entity不合法");
        }*/
        timer.setEntity(body.getEntity());
        timer.setCode(body.getCode());
        timer.setDescription(body.getDescription());
        timer.setName(body.getName());

        String source = body.getState();
        if (source != null) {
            State findById = stateService.queryById(source);
            if (findById == null) {
                throw new BusinessException(2312, "state id不存在");
            }
            timer.setSource_state_id(source);
        }
        if (body.getTimerInterval() == null && body.getTimerOnce() == null) {
            throw new BusinessException(2641, "必须指定时间间隔");
        }

        if (body.getTimerInterval() != null) {
            if (body.getTimerInterval() < 10) {
                throw new BusinessException(1765, "timer间隔不低于10s");
            }
        } else if (body.getTimerOnce() != null) {
            if (body.getTimerOnce() < 10) {
                throw new BusinessException(1765, "timer间隔不低于10s");
            }
        }
        if (!StringUtils.hasText(body.getAction())) {
            throw new BusinessException(1521, "Timer的动作不能为空");
        }
        timer.setAction(body.getAction());
        timer.setTime_interval(body.getTimerInterval());
        timer.setTime_once(body.getTimerOnce());
        timerService.insert(timer);
        return timer.getId();
    }

    @Transactional
    public String stateDeleteTimer(StateMachineController.StateDeleteTimerRequest body) {
        try {
            timerService.deleteById(body.getId());
        } catch (RuntimeException ex) {
            throw new BusinessException(1371, "删除定时器失败");
        }
        return body.getId();
    }

    @Transactional
    public String stateLinkEvent(StateMachineController.StateLinkEvent body) {
        State state = stateService.queryById(body.getState());
        if (state == null) {
            throw new BusinessException(2811, "状态不存在");
        }
        Event event = eventService.queryById(body.getEvent());
        if (event == null) {
            throw new BusinessException(2821, "event不存在");
        }
       /* Set<Event> events = state.getEvents();
        if(events == null) {
            events = new HashSet<Event>();
        }
        events.add(event);*/
        stateService.insert(state);
        return state.getId();
    }

    @Transactional
    public String stateUnlinkEvent(StateMachineController.StateUnlinkEvent body) {
        State state = null;
        Event event = null;
        State findById = stateService.queryById(body.getState());
        if (findById == null) {
            throw new BusinessException(2811, "状态不存在");
        }
        state = findById;
        Event findById2 = eventService.queryById(body.getEvent());
        if (findById2 == null) {
            throw new BusinessException(2821, "event不存在");
        }
      /*  event = findById2;
        Set<Event> events = state.getEvents();
        if (events == null) {
            return null;
        }
        events.remove(event);*/
        stateService.insert(state);
        return state.getId();
    }

    public Object fetchStateGroup() {
        String sql = "1=1 group by entity order by sort";
        SqlBuilder build = new SqlBuilder().append(sql);
        return stateService.queryBySqlBuilder(build);
    }

    public Object fetchEventGroup() {
        String sql = "select * from event group by entity  order by sort";
        return eventService.queryByWhere(sql, null);
    }

    public Object fetchStateMachine(String entity) {
        String sql = "select events,events.roles,events.target,firstTarget,thenTarget,lastTarget,timers , * from state where entity = " + entity + "order by sort asc ";
        return stateService.queryByWhere(sql, null);
    }

    public List<Event> fetchEventList(String entity) {
        String sql = "select target,roles,sources, * from event where entity = " + entity + "  order by sort asc";
        return eventService.queryByWhere(sql, null);
    }
}
