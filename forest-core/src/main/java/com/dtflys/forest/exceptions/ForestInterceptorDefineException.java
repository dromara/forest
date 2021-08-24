package com.dtflys.forest.exceptions;

public class ForestInterceptorDefineException extends ForestRuntimeException {

    private final Class<?> interceptorClass;

    public ForestInterceptorDefineException(Class<?> clazz) {
        super("[Forest] Interceptor class '" + clazz.getName() + "' cannot be initialized, because interceptor class must implements com.dtflys.forest.interceptor.Interceptor");
        this.interceptorClass = clazz;
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }
}
