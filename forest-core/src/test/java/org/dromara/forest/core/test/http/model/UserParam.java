package org.dromara.forest.core.test.http.model;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-15 18:40
 */
public class UserParam {

    private String username;

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

    public String getArgString() {
        return "username=" + username + "&password=" + password;
    }
}
