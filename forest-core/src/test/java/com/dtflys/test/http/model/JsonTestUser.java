package com.dtflys.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

public class JsonTestUser {

    @JSONField(ordinal = 0)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
