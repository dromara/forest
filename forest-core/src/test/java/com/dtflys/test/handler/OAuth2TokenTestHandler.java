package com.dtflys.test.handler;

import com.dtflys.forest.handler.OAuth2DefinitionHandler;
import com.dtflys.forest.lifecycles.authorization.OAuth2Token;

/**
 * 处理认证信息
 *
 * @author YAKAX
 * @since 2021-04-02 18:35
 **/
public class OAuth2TokenTestHandler implements OAuth2DefinitionHandler<DefinitionInfo> {
    @Override
    public OAuth2Token handle(DefinitionInfo object) {
        OAuth2Token token = new OAuth2Token();
        token.setAccess_token(object.getToken());
        return token;
    }

    @Override
    public Class supportJavaTypeKey() {
        return DefinitionInfo.class;
    }


}
