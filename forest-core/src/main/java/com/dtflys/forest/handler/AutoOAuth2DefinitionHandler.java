package com.dtflys.forest.handler;

import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.lifecycles.authorization.OAuth2Token;
import com.dtflys.forest.utils.ForestDataType;

import java.util.Map;


/**
 * auto 赋值
 *
 * @author YAKAX
 * @since 2021-04-02 18:49
 **/
public class AutoOAuth2DefinitionHandler implements OAuth2DefinitionHandler {

    @Override
    public OAuth2Token getOAuth2Token(ForestRequest request, ForestResponse response, Map map) {
        ForestConverter converter = request.getConfiguration().getConverter(ForestDataType.AUTO);
        return (OAuth2Token) converter.convertToJavaObject(response.getContent(), OAuth2Token.class);
    }
}
