package com.dtflys.forest.utils;

import com.dtflys.forest.beans.ClientFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 20:00
 */
public class ClientFactoryBeanUtils {

    private final static Class CLIENT_FACTORY_BEAN_CLASS = ClientFactoryBean.class;

    public static void setupClientFactoryBean(AbstractBeanDefinition beanDefinition, String configurationId, String clientClassName) {
        beanDefinition.setBeanClass(CLIENT_FACTORY_BEAN_CLASS);
        beanDefinition.getPropertyValues().add("forestConfiguration", new RuntimeBeanReference(configurationId));
        beanDefinition.getPropertyValues().add("interfaceClass", clientClassName);
    }
}
