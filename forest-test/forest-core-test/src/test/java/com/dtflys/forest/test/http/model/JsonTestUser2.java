package com.dtflys.forest.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

public class JsonTestUser2 {

    private String Username;

    @JSONField(name = "Username")
    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
