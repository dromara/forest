package org.dromara.forest.core.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;

public class JsonTestUser3 extends JsonTestUser {

    @JSONField(ordinal = 1)
    private String password;

    @JSONField(name = "cn_name", ordinal = 2)
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
