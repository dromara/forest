package com.dtflys.forest.auth;

import com.dtflys.forest.http.ForestRequest;

/**
 * Forest 请求认证器
 *
 * @author gongjun
 * @since 1.5.28
 */
public interface ForestAuthenticator {

    /**
     * 增强请求的认证信息
     *
     * @param request Forest 请求对象
     */
    void enhanceAuthorization(ForestRequest request);
}
