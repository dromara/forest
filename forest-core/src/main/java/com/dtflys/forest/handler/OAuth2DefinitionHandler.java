package com.dtflys.forest.handler;

import com.dtflys.forest.lifecycles.authorization.OAuth2Token;

/**
 * 自定义OAuth2响应
 *
 * @author YAKAX
 * @since 2021-04-02 18:22
 **/
public interface OAuth2DefinitionHandler<T> {

    /**
     * 处理认证响应实体
     *
     * @param object 实体
     * @return forest 认证实体
     */
    OAuth2Token handle(T object);
}
