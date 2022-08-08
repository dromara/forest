package com.dtflys.test.http.model;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.utils.Base64Utils;

public class LoginInfo {

    @Body(order = 1)
    private String username;

    @Body(order = 2)
    private String pass;

    @Body(name = "ts", order = 3)
    private long timestamp;


    @Header("Token")
    public String getToken() {
        return Base64Utils.encode(username + "," + pass + "," + timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
