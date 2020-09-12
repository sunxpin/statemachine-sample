package com.baoxian.common.statemachine;

import com.baoxian.common.annotation.StateMachineAction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
public class QuartzTimerJob implements Job {
    //quartz  job 的执行函数
    @Override
    public void execute(JobExecutionContext context) {
        String id = context.getJobDetail().getJobDataMap().get("id").toString();
        String action = context.getJobDetail().getJobDataMap().getString("action");
        String entity = context.getJobDetail().getJobDataMap().getString("entity");
        BaseStatemachineService stateMachineService = BaseStatemachineService.services.get(entity);
        try {
            Method method = stateMachineService.getClass().getDeclaredMethod(action, Long.class, StateMachine.class);
            StateMachineAction annotation = method.getDeclaredAnnotation(StateMachineAction.class);
            if (annotation == null) {
                throw new RuntimeException(entity + "服务 ," + action + "的方法，必须使用StateMachineAction注解，才能生效");
            }
            try {
                method.invoke(stateMachineService, id, stateMachineService.acquireStateMachine(id));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(entity + "服务 ," + action + "的方法，非法访问");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new RuntimeException(entity + "服务 ," + action + "的方法，非法参数");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getCause().getMessage());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(entity + "服务，没有" + action + "的方法");
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(entity + "服务，禁止访问" + action + "方法");
        }
    }
}