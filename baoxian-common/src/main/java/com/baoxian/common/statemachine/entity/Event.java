package com.baoxian.common.statemachine.entity;

import com.baoxian.common.annotation.Table;
import com.baoxian.common.entity.BaseEntity;


@Table("event")
public class Event extends BaseEntity<Event> {

    private Long version;

    private String entity;

    private String name;

    private String code;

    private String description;

    private String sources;

    private String target_id;

    private String guard_spel;

    private String action;

    private Integer sort = 0;

    private String terminal;

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

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public String getGuard_spel() {
        return guard_spel;
    }

    public void setGuard_spel(String guard_spel) {
        this.guard_spel = guard_spel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
}
