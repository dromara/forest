package org.dromara.forest.schema;

import org.dromara.forest.beans.ClientFactoryBean;
import org.dromara.forest.utils.ClientFactoryBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger log = LoggerFactory.getLogger(ForestClientBeanDefinitionParser.class);

    private final Class factoryBeanClass = ClientFactoryBean.class;

    @Override
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
                + "' and Proxy of '" + clientClassName + "' client interface");

        return beanDefinition;
    }
}
