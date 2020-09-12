package com.baoxian.common.statemachine.entity;

import com.baoxian.common.entity.BaseEntity;

public class StateMachineEntity<T> extends BaseEntity<T> {
    private String state;
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}
