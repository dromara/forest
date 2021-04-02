package com.dtflys.forest.handler;

import com.dtflys.forest.lifecycles.authorization.OAuth2Token;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;

import java.lang.reflect.InvocationTargetException;

/**
 * auto 赋值
 *
 * @author YAKAX
 * @since 2021-04-02 18:49
 **/
public class AutoOAuth2DefinitionHandler implements OAuth2DefinitionHandler {
    @Override
    public OAuth2Token handle(Object object) {
        OAuth2Token token = new OAuth2Token();
        BeanUtilsBean instance = BeanUtilsBean.getInstance();
        try {
            instance.copyProperties(object, token);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return token;
    }
}
