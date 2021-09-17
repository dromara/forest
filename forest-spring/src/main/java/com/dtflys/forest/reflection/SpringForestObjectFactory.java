package com.dtflys.forest.reflection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-18 1:00
 */
public class SpringForestObjectFactory extends DefaultObjectFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T newInstance(Class<T> clazz) {
        T bean = null;
        try {
            bean = applicationContext.getBean(clazz);
        } catch (Throwable ignored) {}
        if (bean == null) {
            super.newInstance(clazz);
        }
        return bean;
    }
}
