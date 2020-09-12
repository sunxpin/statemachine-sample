package com.baoxian.service;

import com.baoxian.common.annotation.StateMachineAction;
import com.baoxian.common.statemachine.BaseStatemachineService;
import com.baoxian.controller.UserController;
import com.baoxian.entity.User;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService extends BaseStatemachineService<User> {
    @Override
    protected void init() {

    }

    @StateMachineAction
    public void doEnable(String id , StateMachine<String,String> stateMachine) {
        User user = queryById(id);
        user.setEnabled(true);
        insert(user);
    }

    @StateMachineAction
    public void doDisable(String id , StateMachine<String,String> stateMachine) {
        User user = queryById(id);
        user.setEnabled(false);
        insert(user);
        this.doKick(id, stateMachine);
    }

    @StateMachineAction
    public void doSpeak(String id , StateMachine<String,String> stateMachine) {
        System.out.println("说话定时器："+new Date());
    }

    @StateMachineAction
    public void doTest(String id , StateMachine<String,String> stateMachine) {
        System.out.println("StateMachineAction测试方法");
    }


    @StateMachineAction
    public void doKick(String id, StateMachine<String,String> stateMachine) {
        User user = queryById(id);
        System.out.println("用户" + user + "被踢下线");
    }

    public Object updateById(String id, String nick, UserController.Gender gender, String faceUrl, String selfSignature) {
        return null;
    }
}
