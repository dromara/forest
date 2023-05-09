package org.dromara.forest.handler;

import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.lifecycles.authorization.OAuth2Token;

import java.util.Map;

/**
 * 自定义OAuth2响应
 *
 * @author YAKAX
 * @since 2021-04-02 18:22
 **/
public interface OAuth2DefinitionHandler {

    /**
     * 处理认证响应实体
     *
     * @param response forest请求返回包装信息
     * @param map      用户OAUTH2返回信息 map
     * @return token对象
     */
    OAuth2Token getOAuth2Token(ForestResponse<String> response, Map map);
}
