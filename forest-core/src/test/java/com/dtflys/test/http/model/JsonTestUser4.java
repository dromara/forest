package com.dtflys.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.dtflys.forest.annotation.NotNull;

public class JsonTestUser4 extends JsonTestUser {

    @JSONField(ordinal = 1)
    private String password;


    @JSONField(name = "cn_name", ordinal = 2)
    @NotNull
    private String cnName;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }
}
