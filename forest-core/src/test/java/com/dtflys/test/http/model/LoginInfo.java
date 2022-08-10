package com.dtflys.test.http.model;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Headers;
import com.dtflys.forest.utils.Base64Utils;

@Headers({"Content-Type: application/json"})
public class LoginInfo {

    @Body
    private String username;

    @Body
    private String pass;

    @Body("ts")
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
