package com.dtflys.forest.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-21 14:46
 */
public class ForestNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("configuration", new ForestConfigurationBeanDefinitionParser());
        registerBeanDefinitionParser("client0", new ForestClientBeanDefinitionParser());
        registerBeanDefinitionParser("scan", new ForestScanBeanDefinitionParser());
    }
}
