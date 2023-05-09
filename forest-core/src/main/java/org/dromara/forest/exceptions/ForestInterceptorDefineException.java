package org.dromara.forest.exceptions;

public class ForestInterceptorDefineException extends ForestRuntimeException {

    private final Class<?> interceptorClass;

    public ForestInterceptorDefineException(Class<?> clazz) {
        super("[Forest] Interceptor class '" + clazz.getName() + "' cannot be initialized, because interceptor class must implements org.dromara.forest.interceptor.Interceptor");
        this.interceptorClass = clazz;
    }

    public Class<?> getInterceptorClass() {
        return interceptorClass;
    }
}
