package com.baoxian.controller;

import com.baoxian.common.annotation.StateMachineAction;
import com.baoxian.common.bean.Datagrid;
import com.baoxian.common.bean.Page;
import com.baoxian.common.statemachine.BaseStatemachineService;
import com.baoxian.common.statemachine.entity.Event;
import com.baoxian.common.statemachine.entity.State;
import com.baoxian.exception.BusinessException;
import com.baoxian.service.StateMachineManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/stateMachine")
@Api(tags = "管理状态机的接口")
public class StateMachineController {

    @Autowired
    private StateMachineManagementService stateMachineManagementService;

    @PostMapping(path = "/addState")
    @ApiOperation(value = "添加状态", notes = "新增一个state", httpMethod = "POST")
    public ResponseEntity addState(@Valid @RequestBody AddStateRequest body) {
        return new ResponseEntity(stateMachineManagementService.addState(body), HttpStatus.OK);
    }

    @PostMapping(path = "/deleteState")
    @ApiOperation(value = "删除状态", notes = "删除一个state", httpMethod = "POST")
    public ResponseEntity deleteState(@Valid @RequestBody(required = true) DeleteStateRequest body) {
        return new ResponseEntity(stateMachineManagementService.deleteState(body), HttpStatus.OK);
    }

    @PostMapping(path = "/updateState")
    @ApiOperation(value = "更新状态", notes = "更新一个state,把所有字段重新提交一次", httpMethod = "POST")
    public ResponseEntity updateState(@Valid @RequestBody(required = true) UpdateStateRequest body) {
        return new ResponseEntity(stateMachineManagementService.updateState(body), HttpStatus.OK);
    }

    @PostMapping(path = "/updateCache/{entity}")
    @ApiOperation(value = "更新缓存", notes = "每次重新更改设置后，需要更新缓存才能生效", httpMethod = "POST")
    public ResponseEntity updateCache(@PathVariable(required = true) String entity) {
        stateMachineManagementService.updateCache(entity);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path = "/addEvent")
    @ApiOperation(value = "添加事件", notes = "新增一个event,可执行角色也一并设置", httpMethod = "POST")
    public ResponseEntity addEvent(@Valid @RequestBody(required = true) AddEventRequest body) {
        return new ResponseEntity(stateMachineManagementService.addEvent(body), HttpStatus.OK);
    }

    @PostMapping(path = "/deleteEvent")
    @ApiOperation(value = "删除事件", notes = "删除一个event", httpMethod = "POST")
    public ResponseEntity deleteEvent(@Valid @RequestBody(required = true) DeleteEventRequest body) {
        return new ResponseEntity(stateMachineManagementService.deleteEvent(body), HttpStatus.OK);
    }

    @PostMapping(path = "/updateEvent")
    @ApiOperation(value = "更新事件", notes = "更新一个event，把所有字段重新提交一次", httpMethod = "POST")
    public ResponseEntity updateState(@Valid @RequestBody(required = true) UpdateEventRequest body) {
        return new ResponseEntity(stateMachineManagementService.updateEvent(body), HttpStatus.OK);
    }

    @PostMapping(path = "/stateAddTimer")
    @ApiOperation(value = "添加定时器", notes = "新增一个timer，定时器附属于state，不复用", httpMethod = "POST")
    public ResponseEntity stateAddTimer(@Valid @RequestBody(required = true) StateAddTimerRequest body) {
        return new ResponseEntity(stateMachineManagementService.stateAddTimer(body), HttpStatus.OK);
    }

    @PostMapping(path = "/stateDeleteTimer")
    @ApiOperation(value = "删除定时器", notes = "删除一个timer,删除定时器时候，已经生效的定时器不会删除", httpMethod = "POST")
    public ResponseEntity stateDeleteTimer(@Valid @RequestBody(required = true) StateDeleteTimerRequest body) {
        return new ResponseEntity(stateMachineManagementService.stateDeleteTimer(body), HttpStatus.OK);
    }

    @PostMapping(path = "/stateLinkEvent")
    @ApiOperation(value = "状态添加事件", notes = "给状态新增一个事件处理能力", httpMethod = "POST")
    public ResponseEntity stateLinkEvent(@Valid @RequestBody(required = true) StateLinkEvent body) {
        return new ResponseEntity(stateMachineManagementService.stateLinkEvent(body), HttpStatus.OK);
    }

    @PostMapping(path = "/stateUnlinkEvent")
    @ApiOperation(value = "状态取消事件", notes = "状态删除一个它的事件", httpMethod = "POST")
    public ResponseEntity stateUnlinkEvent(@Valid @RequestBody(required = true) StateUnlinkEvent body) {
        return new ResponseEntity(stateMachineManagementService.stateUnlinkEvent(body), HttpStatus.OK);
    }

    @GetMapping(path = "/list")
    @ApiOperation(value = "状态机种类", notes = "返回一个列表", httpMethod = "GET")
    public ResponseEntity fetchStateList() {
        return new ResponseEntity(stateMachineManagementService.fetchStateGroup(), HttpStatus.OK);
    }

    @GetMapping(path = "/listEvent")
    @ApiOperation(value = "状态机事件分组显示", notes = "返回一个分组列表", httpMethod = "GET")
    public ResponseEntity listEvent() {
        return new ResponseEntity(stateMachineManagementService.fetchEventGroup(), HttpStatus.OK);
    }

    @GetMapping(path = "/list/{entity}")
    @ApiOperation(value = "状态机的细节展示", notes = "根据Entity参数，返回对应的状态机的状态-事件细节", httpMethod = "GET")
    public ResponseEntity fetchStateMachine(@PathVariable(required = true) String entity) {
        return new ResponseEntity(stateMachineManagementService.fetchStateMachine(entity), HttpStatus.OK);
    }

    @GetMapping(path = "/list/{entity}/action")
    @ApiOperation(value = "Java中，已经编写的可执行动作", notes = "Java程序已经编写好的动作函数名称", httpMethod = "GET")
    public ResponseEntity fetchStateMachineAction(@PathVariable(required = true) String entity) {
        HashMap<String, BaseStatemachineService> services = BaseStatemachineService.services;
        BaseStatemachineService stateMachineService = services.get(entity);
        if (stateMachineService == null) {
            throw new BusinessException(2766, "实体状态机不存在");
        }

        Datagrid datagrid = new Datagrid();
        ArrayList<String> arrayList = new ArrayList<String>();
        Method[] declaredMethods = stateMachineService.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            StateMachineAction annotation = method.getDeclaredAnnotation(StateMachineAction.class);
            if (annotation != null) {
                arrayList.add(method.getName());
            }
        }
        datagrid.setData(arrayList);
        Page page = new Page();
        page.setTotalRecords(arrayList.size());
        datagrid.setPage(page);
        return new ResponseEntity(datagrid, HttpStatus.OK);
    }

    @GetMapping(path = "/list/{entity}/uselessEvent")
    @ApiOperation(value = "状态机还没有使用的事件列表", notes = "", httpMethod = "GET")
    public ResponseEntity fetchStateMachineUselessEvent(@PathVariable(required = true) String entity) {
        List<Event> eventList = stateMachineManagementService.fetchEventList(entity);
        Datagrid datagrid = new Datagrid();
        if (eventList.size() > 0) {
            List collect = (List) eventList.stream().filter(e -> ((Map) e).get("sources").equals(Collections.EMPTY_MAP)).collect(Collectors.toList());
            datagrid.setData(collect);
            Page page = new Page();
            page.setTotalRecords(collect.size());
            datagrid.setPage(page);
        }
        return new ResponseEntity(datagrid, HttpStatus.OK);
    }

    public static class StateLinkEvent{
        @javax.validation.constraints.NotNull
        private String state;
        @javax.validation.constraints.NotNull
        private String event;
        public String getState() {
            return state;
        }
        public void setState(String state) {
            this.state = state;
        }
        public String getEvent() {
            return event;
        }
        public void setEvent(String event) {
            this.event = event;
        }
    }
    public static class StateUnlinkEvent{
        @javax.validation.constraints.NotNull
        private String state;
        @javax.validation.constraints.NotNull
        private String event;
        public String getState() {
            return state;
        }
        public void setState(String state) {
            this.state = state;
        }
        public String getEvent() {
            return event;
        }
        public void setEvent(String event) {
            this.event = event;
        }
    }
    public static class StateDeleteTimerRequest {
        @javax.validation.constraints.NotNull
        private String id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

    public static class StateAddTimerRequest {
        @NotBlank
        private String entity;
        @NotBlank
        private String name;
        @NotBlank
        private String code;
        @Column(name = "description")
        private String description;
        @javax.validation.constraints.NotNull
        private String state;
        private String action;
        private Integer timerInterval;
        private Integer timerOnce;
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
        public String getState() {
            return state;
        }
        public void setState(String state) {
            this.state = state;
        }
        public String getAction() {
            return action;
        }
        public void setAction(String action) {
            this.action = action;
        }
        public Integer getTimerInterval() {
            return timerInterval;
        }
        public void setTimerInterval(Integer timerInterval) {
            this.timerInterval = timerInterval;
        }
        public Integer getTimerOnce() {
            return timerOnce;
        }
        public void setTimerOnce(Integer timerOnce) {
            this.timerOnce = timerOnce;
        }

    }

    public static class UpdateEventRequest extends AddEventRequest {
        @javax.validation.constraints.NotNull
        private String id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

    public static class DeleteEventRequest {
        @javax.validation.constraints.NotNull
        private String id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

    public static class AddEventRequest {
        @NotBlank
        private String entity;
        @NotBlank
        private String name;
        @NotBlank
        private String code;
        @Column(name = "description")
        private String description;
        private String target;
        private String guardSpel;
        @Column(name = "action")
        private String action;
        @Column(name = "sort")
        private Integer sort = 0;
        private Set<Long> roles;
        @javax.validation.constraints.NotNull
        private String terminal;
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
        public String getGuardSpel() {
            return guardSpel;
        }
        public void setGuardSpel(String guardSpel) {
            this.guardSpel = guardSpel;
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
        public Set<Long> getRoles() {
            return roles;
        }
        public void setRoles(Set<Long> roles) {
            this.roles = roles;
        }
        public String getTarget() {
            return target;
        }
        public void setTarget(String target) {
            this.target = target;
        }
        public String getTerminal() {
            return terminal;
        }
        public void setTerminal(String terminal) {
            this.terminal = terminal;
        }
    }

    public static class UpdateStateRequest extends AddStateRequest {
        @javax.validation.constraints.NotNull
        private String id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

    public static class DeleteStateRequest {
        @javax.validation.constraints.NotNull
        private String id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

    public static class AddStateRequest {
        @NotBlank
        private String entity;
        @NotBlank
        private String name;
        @NotBlank
        private String code;
        private String description;
        @Enumerated(EnumType.STRING)
        private State.StateType stateType = State.StateType.COMMON;
        private String firstTarget;
        private String firstGuardSpel;
        private String thenTarget;
        private String thenGuardSpel;
        private String lastTarget;
        private String enterAction;
        private String exitAction;
        private Integer sort = 0;
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
        public State.StateType getStateType() {
            return stateType;
        }
        public void setStateType(State.StateType stateType) {
            this.stateType = stateType;
        }
        public String getFirstTarget() {
            return firstTarget;
        }
        public void setFirstTarget(String firstTarget) {
            this.firstTarget = firstTarget;
        }
        public String getThenTarget() {
            return thenTarget;
        }
        public void setThenTarget(String thenTarget) {
            this.thenTarget = thenTarget;
        }
        public String getLastTarget() {
            return lastTarget;
        }
        public void setLastTarget(String lastTarget) {
            this.lastTarget = lastTarget;
        }
        public String getFirstGuardSpel() {
            return firstGuardSpel;
        }
        public void setFirstGuardSpel(String firstGuardSpel) {
            this.firstGuardSpel = firstGuardSpel;
        }
        public String getThenGuardSpel() {
            return thenGuardSpel;
        }
        public void setThenGuardSpel(String thenGuardSpel) {
            this.thenGuardSpel = thenGuardSpel;
        }
        public String getEnterAction() {
            return enterAction;
        }
        public void setEnterAction(String enterAction) {
            this.enterAction = enterAction;
        }
        public String getExitAction() {
            return exitAction;
        }
        public void setExitAction(String exitAction) {
            this.exitAction = exitAction;
        }
        public Integer getSort() {
            return sort;
        }
        public void setSort(Integer sort) {
            this.sort = sort;
        }
    }
}