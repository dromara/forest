package org.dromara.forest.handler;

import org.dromara.forest.converter.ForestConverter;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.lifecycles.authorization.OAuth2Token;
import org.dromara.forest.utils.ForestDataType;

import java.util.Map;


/**
 * auto 赋值
 *
 * @author YAKAX
 * @since 2021-04-02 18:49
 **/
public class AutoOAuth2DefinitionHandler implements OAuth2DefinitionHandler {

    @Override
    public OAuth2Token getOAuth2Token(ForestResponse<String> response, Map map) {
        final ForestConverter converter = response.getRequest().getConfiguration().getConverter(ForestDataType.AUTO);
        return (OAuth2Token) converter.convertToJavaObject(response.getContent(), OAuth2Token.class);
    }
}
