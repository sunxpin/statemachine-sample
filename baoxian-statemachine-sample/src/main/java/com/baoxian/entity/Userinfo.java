package com.baoxian.entity;

import com.baoxian.common.annotation.Table;
import com.baoxian.common.entity.BaseEntity;

import javax.persistence.Column;


@Table("user_info")
public class Userinfo extends BaseEntity<Userinfo> {

    @Column(name = "version")
    private Long version;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "password")
    private String password;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}