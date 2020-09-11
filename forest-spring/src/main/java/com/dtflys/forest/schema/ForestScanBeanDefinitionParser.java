package com.dtflys.forest.schema;

import com.dtflys.forest.scanner.ClassPathClientScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-21 17:49
 */
public class ForestScanBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String configurationId = element.getAttribute("configuration");
        ClassPathClientScanner scanner = new ClassPathClientScanner(configurationId, parserContext.getRegistry());
        String basePackage = element.getAttribute("base-package");
        scanner.scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        return null;
    }
}
