package com.dtflys.forest.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

public class FormArrayParam {

    @JSONField(ordinal = 1)
    private String username;

    @JSONField(ordinal = 2)
    private String password;

    @JSONField(ordinal = 3)
    private Integer[] idList;

    @JSONField(ordinal = 4)
    private Cause[] cause;

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

    public Integer[] getIdList() {
        return idList;
    }

    public void setIdList(Integer[] idList) {
        this.idList = idList;
    }

    public Cause[] getCause() {
        return cause;
    }

    public void setCause(Cause[] cause) {
        this.cause = cause;
    }
}
