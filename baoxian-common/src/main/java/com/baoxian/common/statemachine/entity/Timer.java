package com.baoxian.common.statemachine.entity;

import com.baoxian.common.annotation.Table;
import com.baoxian.common.entity.BaseEntity;

@Table("timer")
public class Timer extends BaseEntity<Timer> {

    private Long version;

    private String entity;

    private String name;

    private String code;

    private String description;

    private String source_state_id;

    private String action;

    private Integer time_interval;

    private Integer time_once;

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

    public String getSource_state_id() {
        return source_state_id;
    }

    public void setSource_state_id(String source_state_id) {
        this.source_state_id = source_state_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getTime_interval() {
        return time_interval;
    }

    public void setTime_interval(Integer time_interval) {
        this.time_interval = time_interval;
    }

    public Integer getTime_once() {
        return time_once;
    }

    public void setTime_once(Integer time_once) {
        this.time_once = time_once;
    }
}
