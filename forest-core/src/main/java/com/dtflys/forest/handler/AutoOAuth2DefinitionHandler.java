package com.dtflys.forest.handler;

import com.dtflys.forest.lifecycles.authorization.OAuth2Token;


/**
 * auto 赋值
 *
 * @author YAKAX
 * @since 2021-04-02 18:49
 **/
public class AutoOAuth2DefinitionHandler implements OAuth2DefinitionHandler<OAuth2Token> {
    @Override
    public OAuth2Token handle(OAuth2Token object) {
        return object;
    }

    @Override
    public Class supportJavaTypeKey() {
        return OAuth2Token.class;
    }
}
