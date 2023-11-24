package com.dtflys.test.http.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-15 18:40
 */
public class UserParam {

    private String username;

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
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

    public String argString() {
        return "username=" + username + "&password=" + password;
    }
}
