package org.dromara.forest.core.test.model;

public class TokenResult {

    private long TokenTimeout;

    private String URLToken;

    public long getTokenTimeout() {
        return TokenTimeout;
    }

    public void setTokenTimeout(long tokenTimeout) {
        TokenTimeout = tokenTimeout;
    }

    public String getURLToken() {
        return URLToken;
    }

    public void setURLToken(String URLToken) {
        this.URLToken = URLToken;
    }
}
