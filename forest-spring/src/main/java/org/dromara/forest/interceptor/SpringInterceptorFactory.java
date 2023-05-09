package org.dromara.forest.interceptor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringInterceptorFactory extends DefaultInterceptorFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    protected <T extends Interceptor> Interceptor createInterceptor(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Throwable th) {
            return super.createInterceptor(clazz);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
