package com.dtflys.forest.auth;

import com.dtflys.forest.http.ForestRequest;

/**
 * Forest Bearer 认证器
 * <p>为请求提供 Bearer 认证信息</p>
 *
 * @author gongjun
 * @since 1.6.5
 */
public class BearerAuth extends ForestAuthenticator {
    
    private String token;
    
    public static BearerAuth token(String token) {
        return new BearerAuth(token);
    }

    public BearerAuth(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public BearerAuth setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public void enhanceAuthorization(ForestRequest request) {
        String bearer = "Bearer " + token;
        request.addHeader("Authorization", bearer);
    }
}
