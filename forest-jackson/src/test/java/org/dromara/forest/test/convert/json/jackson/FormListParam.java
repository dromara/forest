package org.dromara.forest.test.convert.json.jackson;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"username", "password", "idList", "cause"})
public class FormListParam {

    private String username;

    private String password;

    private List<Integer> idList;

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
