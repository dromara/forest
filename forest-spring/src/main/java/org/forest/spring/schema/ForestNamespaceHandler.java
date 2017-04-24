package org.forest.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-21 14:46
 */
public class ForestNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("configuration", new ForestConfigurationBeanDefinitionParser());
        registerBeanDefinitionParser("scan", new ForestScanBeanDefinitionParser());
    }
}
