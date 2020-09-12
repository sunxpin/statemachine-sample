package com.baoxian.common.statemachine.entity;

import com.baoxian.common.annotation.Table;
import com.baoxian.common.entity.BaseEntity;

@Table("state_event")
public class StateEvent extends BaseEntity<StateEvent> {

    private String state_id;
    private String event_id;

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}
