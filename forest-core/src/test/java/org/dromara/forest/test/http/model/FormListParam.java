package org.dromara.forest.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class FormListParam {

    @JSONField(ordinal = 1)
    private String username;

    @JSONField(ordinal = 2)
    private String password;

    @JSONField(ordinal = 3)
    private List<Integer> idList;

    @JSONField(ordinal = 4)
    private List<Cause> cause;

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

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    public List<Cause> getCause() {
        return cause;
    }

    public void setCause(List<Cause> cause) {
        this.cause = cause;
    }
}
