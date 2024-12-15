package com.dtflys.forest.auth;

import com.dtflys.forest.http.ForestRequest;

/**
 * Forest 请求认证器
 *
 * @author gongjun
 * @since 1.5.28
 */
public abstract class ForestAuthenticator {

    private ForestAuthenticator next;

    /**
     * 增强请求的认证信息
     *
     * @param request Forest 请求对象
     */
    public abstract void enhanceAuthorization(ForestRequest request);

    public final ForestAuthenticator next(ForestAuthenticator next) {
        this.next = next;
        return this;
    }

    public ForestAuthenticator next() {
        return next;
    }
}
