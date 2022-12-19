package com.dtflys.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

public class JsonTestUser {

    @JSONField(ordinal = 0)
    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
