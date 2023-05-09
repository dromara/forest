package org.dromara.test.http.model;

import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.Header;
import org.dromara.forest.annotation.Headers;
import org.dromara.forest.utils.Base64Utils;

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
