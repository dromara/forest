package com.dtflys.forest.interceptor;

public interface InterceptorFactory {

    InterceptorChain getInterceptorChain();

    <T extends Interceptor> T getInterceptor(Class<T> clazz);
}
