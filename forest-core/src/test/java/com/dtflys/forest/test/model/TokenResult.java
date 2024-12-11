package com.dtflys.forest.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResult {

    @JsonProperty("TokenTimeout")
    private long TokenTimeout;

    @JsonProperty("URLToken")
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
