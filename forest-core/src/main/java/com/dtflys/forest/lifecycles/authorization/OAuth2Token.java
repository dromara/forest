package com.dtflys.forest.lifecycles.authorization;

import java.io.Serializable;

/**
 * OAuth2 Token
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA9
 */
public class OAuth2Token implements Serializable {

    /**
     * access token字符串
     */
    private String access_token;

    /**
     * 超时时间
     */
    private Long expires_in;

    /**
     * refresh token字符串
     */
    private String refresh_token;

    /**
     * token类型
     */
    private String token_type;

    /**
     * 错误信息
     */
    private Object error;

    /**
     * 第二种错信息
     */
    private Object errcode;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Object getErrcode() {
        return errcode;
    }

    public void setErrcode(Object errcode) {
        this.errcode = errcode;
    }
}
