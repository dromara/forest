package com.dtflys.forest.auth;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.Base64Utils;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest BasicAuth 认证器
 * <p>为请求提供 BasicAuth 认证信息</p>
 *
 * @author gongjun
 * @since 1.5.28
 */
public class BasicAuth extends ForestAuthenticator {

    private String userInfo;

    public BasicAuth() {
    }

    public BasicAuth(final String userInfo) {
        this.userInfo = userInfo;
    }

    public BasicAuth(final String username, final String password) {
        this.userInfo = username + ":" + password;
    }

    public static BasicAuth user(final String username, final String password) {
        return new BasicAuth(username, password);
    }

    public static BasicAuth userInfo(final String userInfo) {
        return new BasicAuth(userInfo);
    }


    public String setUserInfo() {
        return userInfo;
    }

    public BasicAuth setUserInfo(final String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public BasicAuth setUserInfo(final String username, final String password) {
        this.userInfo = username + ":" + password;
        return this;
    }

    public String getUserInfo() {
        return userInfo;
    }

    @Override
    public void enhanceAuthorization(ForestRequest request) {
        String userInfo = this.userInfo;
        if (StringUtils.isEmpty(userInfo)) {
            userInfo = request.getUserInfo();
        }
        if (StringUtils.isNotEmpty(userInfo)) {
            String basic = "Basic " + Base64Utils.encode(userInfo);
            request.addHeader("Authorization", basic);
        }
    }

}
