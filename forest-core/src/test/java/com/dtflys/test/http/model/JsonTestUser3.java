package com.dtflys.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

public class JsonTestUser3 extends JsonTestUser {

    @JSONField(ordinal = 1)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
