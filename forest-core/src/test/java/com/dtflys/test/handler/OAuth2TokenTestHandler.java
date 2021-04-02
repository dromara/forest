package com.dtflys.test.handler;

import com.dtflys.forest.handler.OAuth2DefinitionHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.lifecycles.authorization.OAuth2Token;

import java.util.Map;

/**
 * 处理认证信息
 *
 * @author YAKAX
 * @since 2021-04-02 18:35
 **/
public class OAuth2TokenTestHandler implements OAuth2DefinitionHandler {

    @Override
    public OAuth2Token getOAuth2Token(ForestRequest request, ForestResponse response, Map map) {
        OAuth2Token oAuth2Token = new OAuth2Token();
        oAuth2Token.setAccess_token((String) map.get("token"));
        return oAuth2Token;
    }
}
