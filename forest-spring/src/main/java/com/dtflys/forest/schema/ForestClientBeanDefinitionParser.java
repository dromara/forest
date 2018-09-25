package com.dtflys.forest.schema;

import com.dtflys.forest.beans.ClientFactoryBean;
import com.dtflys.forest.utils.ClientFactoryBeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 19:49
 */
public class ForestClientBeanDefinitionParser implements BeanDefinitionParser {
    private static Log log = LogFactory.getLog(ForestConfigurationBeanDefinitionParser.class);

    private final Class factoryBeanClass = ClientFactoryBean.class;

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();

        beanDefinition.setLazyInit(false);
        String id = element.getAttribute("id");
        id = ClientFactoryBeanUtils.getBeanId(id, factoryBeanClass, parserContext);
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id))  {
                throw new IllegalStateException("Duplicate spring bean id " + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
//            beanDefinition.getPropertyValues().addPropertyValue("id", id);
        }

        String configurationId = element.getAttribute("configuration");
        String clientClassName = element.getAttribute("class");

        ClientFactoryBeanUtils.setupClientFactoryBean(beanDefinition, configurationId, clientClassName);
        log.info("[Forest] Created Forest Client Bean with name '" + id
                + "' and Proxy of '" + clientClassName + "' client0 interface");

        return beanDefinition;
    }
}
