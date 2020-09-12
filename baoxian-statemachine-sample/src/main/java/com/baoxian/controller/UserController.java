package com.baoxian.controller;


import com.baoxian.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/kick")
    @ApiOperation(value = "踢下线", notes = "管理员使用，如果用户在线则直接踢下线", httpMethod = "POST")
    public ResponseEntity kick(@Valid @RequestBody(required = true) ReasonBody body) {
        userService.dispatchEvent(body.getId(), "kick", body);
        return new ResponseEntity(body.getId(), HttpStatus.OK);
    }

    @PostMapping(path = "/enable")
    @ApiOperation(value = "启用用户", notes = "管理员使用，如果用户被禁用状态下，可以启用", httpMethod = "POST")
    public ResponseEntity enable(@Valid @RequestBody(required = true) ReasonBody body) {
        userService.dispatchEvent(body.getId(), "enable", body);
        return new ResponseEntity(body.getId(), HttpStatus.OK);
    }

    @PostMapping(path = "/disable")
    @ApiOperation(value = "禁用用户", notes = "管理员使用，如果用户启用状态下，可以禁用账号", httpMethod = "POST")
    public ResponseEntity disable(@Valid @RequestBody(required = true) ReasonBody body) {
        userService.dispatchEvent(body.getId(), "disable", body);
        return new ResponseEntity(body.getId(), HttpStatus.OK);
    }

    @PostMapping(path = "/update")
    @ApiOperation(value = "更新自己的用户资料", notes = "更新用户的基本资料，不包括角色和部门", httpMethod = "POST")
    public ResponseEntity update(@Valid @RequestBody(required = true) UserBody body) {
        return new ResponseEntity(userService.updateById(body.getId(), body.getNick(), body.getGender(), body.getFaceUrl(), body.getSelfSignature()), HttpStatus.OK);
    }

    public static class UserBody {
        @NotNull
        private String id;
        private String nick;
        private Gender gender;
        private String faceUrl;
        private String selfSignature;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getFaceUrl() {
            return faceUrl;
        }

        public void setFaceUrl(String faceUrl) {
            this.faceUrl = faceUrl;
        }

        public String getSelfSignature() {
            return selfSignature;
        }

        public void setSelfSignature(String selfSignature) {
            this.selfSignature = selfSignature;
        }
    }

    public static class ReasonBody {
        @NotNull
        private String id;
        @NotNull
        private String reason;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

    }
    public enum Gender {
        Gender_Type_Unknown, Gender_Type_Female, Gender_Type_Male
    }
}
