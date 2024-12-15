package com.dtflys.forest.beans;

import com.dtflys.forest.config.ForestConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 18:47
 */
public class ClientFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ForestConfiguration forestConfiguration;

    private Class<T> interfaceClass;

    public ForestConfiguration getForestConfiguration() {
        return forestConfiguration;
    }

    public void setForestConfiguration(ForestConfiguration forestConfiguration) {
        this.forestConfiguration = forestConfiguration;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() {
        if (forestConfiguration == null) {
            synchronized (this) {
                if (forestConfiguration == null) {
                    try {
                        forestConfiguration = applicationContext.getBean(ForestConfiguration.class);
                    } catch (Throwable th) {
                    }
                    if (forestConfiguration == null) {
                        forestConfiguration = ForestConfiguration.getDefaultConfiguration();
                    }
                }
            }
        }
        return forestConfiguration.createInstance(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
