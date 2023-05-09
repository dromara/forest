package org.dromara.forest.lifecycles.authorization;

import java.io.Serializable;

/**
 * OAuth2 Token
 *
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
     * 错误代码。
     * 错误代码的KEY，在 Spring Security OAuth2 情况下通常是 error，但是发现在微信公众号的返回是 errcode
     */
    private Object error;

    /**
     * @see #error
     */
    private Object errcode;

    /**
     * 错误内容描述。
     * 错误内容描述的KEY，在 Spring Security OAuth2 情况下通常是 error_description，但是发现在微信公众号的返回是 errmsg
     */
    private Object error_description;

    /**
     * @see #error_description
     */
    private Object errmsg;

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

    public Object getError_description() {
        return error_description;
    }

    public void setError_description(Object error_description) {
        this.error_description = error_description;
    }

    public Object getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(Object errmsg) {
        this.errmsg = errmsg;
    }

    public boolean hasError() {
        return error != null || errcode != null;
    }

    public String getErrorMessage() {
        if (error != null) {
            return "{error='" + error + "', error_description='" + error_description + "'}";
        }
        if (errcode != null) {
            return "{errcode='" + errcode + "', errmsg='" + errmsg + "'}";
        }
        return toString();
    }

    @Override
    public String toString() {
        return "OAuth2Token{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                ", refresh_token='" + refresh_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", error=" + error +
                ", errcode=" + errcode +
                ", error_description=" + error_description +
                ", errmsg=" + errmsg +
                '}';
    }
}
