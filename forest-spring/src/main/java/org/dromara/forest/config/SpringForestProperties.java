package org.dromara.forest.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * Forest配置属性的Spring环境下的实现
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.3
 */
public class SpringForestProperties extends ForestProperties implements ApplicationContextAware {

    private Environment environment;

    @Override
    public String getProperty(String name, String defaultValue) {
        if (environment == null) {
            return super.getProperty(name, defaultValue);
        }
        return environment.getProperty(name, defaultValue);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        environment = applicationContext.getEnvironment();
    }
}
