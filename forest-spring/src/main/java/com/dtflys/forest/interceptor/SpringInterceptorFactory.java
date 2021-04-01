package com.dtflys.forest.interceptor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringInterceptorFactory extends DefaultInterceptorFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    protected <T extends Interceptor> Interceptor createInterceptor(Class<T> clazz) {
        Interceptor interceptor = null;
        try {
            interceptor = applicationContext.getBean(clazz);
        } catch (Throwable th) {}
        if (interceptor != null) {
            interceptorMap.put(clazz, interceptor);
        } else {
            return super.createInterceptor(clazz);
        }
        return interceptor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
