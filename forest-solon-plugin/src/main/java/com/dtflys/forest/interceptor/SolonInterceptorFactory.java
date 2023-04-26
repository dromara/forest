package com.dtflys.forest.interceptor;

import org.noear.solon.core.AopContext;

/**
 * @author noear
 * @since 1.11
 */
public class SolonInterceptorFactory extends DefaultInterceptorFactory {
    final AopContext context;
    public SolonInterceptorFactory(AopContext context){
        this.context = context;
    }

    @Override
    protected <T extends Interceptor> Interceptor createInterceptor(Class<T> clazz) {
        try {
            return context.getBeanOrNew(clazz);
        } catch (Throwable th) {
            return super.createInterceptor(clazz);
        }
    }
}
