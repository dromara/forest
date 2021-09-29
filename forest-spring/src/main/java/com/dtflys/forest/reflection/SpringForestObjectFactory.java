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

    /**
     * 获取Forest接口对象(Spring方式)
     * <p>适用于Forest相关接口(非请求客户端接口)和回调函数的工厂接口
     * <p>当这些类没有实例的情况下，会先实例化并缓存下来，以后再取会通过缓存获取对象
     * <p>实例化方式：通过Spring上下文获取Bean
     *
     * @param clazz Forest对象接口类
     * @param <T> Forest对象接口类泛型
     * @return Forest对象实例
     */
    @Override
    public <T> T getObject(Class<T> clazz) {
        T bean = getObjectFromCache(clazz);
        if (bean == null) {
            try {
                bean = applicationContext.getBean(clazz);
            } catch (Throwable ignored) {
            }
            if (bean == null) {
                bean = super.getObject(clazz);
            }
        }
        return bean;
    }
}
