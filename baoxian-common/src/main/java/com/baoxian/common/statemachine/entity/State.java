package com.baoxian.common.statemachine.entity;

import com.baoxian.common.annotation.Table;
import com.baoxian.common.entity.BaseEntity;


@Table("state")
public class State extends BaseEntity<State> {

    private Long version;

    private String entity;

    private String name;

    private String code;

    private String description;

    private String state_type;

    private String first_target_id;

    private String first_guard_spel;

    private String then_target_id;

    private String then_guard_spel;

    private String last_target_id;

    private String enter_action;

    private String exit_action;

    private Integer sort;

    public enum StateType {
        COMMON, BEGIN, END, CHOICE
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState_type() {
        return state_type;
    }

    public void setState_type(String state_type) {
        this.state_type = state_type;
    }

    public String getFirst_target_id() {
        return first_target_id;
    }

    public void setFirst_target_id(String first_target_id) {
        this.first_target_id = first_target_id;
    }

    public String getFirst_guard_spel() {
        return first_guard_spel;
    }

    public void setFirst_guard_spel(String first_guard_spel) {
        this.first_guard_spel = first_guard_spel;
    }

    public String getThen_target_id() {
        return then_target_id;
    }

    public void setThen_target_id(String then_target_id) {
        this.then_target_id = then_target_id;
    }

    public String getThen_guard_spel() {
        return then_guard_spel;
    }

    public void setThen_guard_spel(String then_guard_spel) {
        this.then_guard_spel = then_guard_spel;
    }

    public String getLast_target_id() {
        return last_target_id;
    }

    public void setLast_target_id(String last_target_id) {
        this.last_target_id = last_target_id;
    }

    public String getEnter_action() {
        return enter_action;
    }

    public void setEnter_action(String enter_action) {
        this.enter_action = enter_action;
    }

    public String getExit_action() {
        return exit_action;
    }

    public void setExit_action(String exit_action) {
        this.exit_action = exit_action;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
