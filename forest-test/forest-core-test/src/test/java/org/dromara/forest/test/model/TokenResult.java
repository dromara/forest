package org.dromara.forest.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResult {

    @JsonProperty("TokenTimeout")
    private long tokenTimeout;


    @JsonProperty("URLToken")
    private String urlToken;

    public long getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(long tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    public String getUrlToken() {
        return urlToken;
    }

    public void setUrlToken(String urlToken) {
        this.urlToken = urlToken;
    }
}
