package org.dromara.forest.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-21 14:46
 */
public class ForestNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("configuration", new ForestConfigurationBeanDefinitionParser());
        registerBeanDefinitionParser("client", new ForestClientBeanDefinitionParser());
        registerBeanDefinitionParser("scan", new ForestScanBeanDefinitionParser());
    }
}
