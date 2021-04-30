package com.dtflys.forest.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * Spring环境中的Properties配置属性
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.5.1
 */
public class SpringForestProperties extends ForestProperties implements ApplicationContextAware {

    private Environment environment;

    @Override
    public String getProperty(String name, String defaultValue) {
        String value = environment.getProperty(name);
        if (value == null) {
            return super.getProperty(name, defaultValue);
        }
        return value;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.environment = applicationContext.getEnvironment();
    }
}
