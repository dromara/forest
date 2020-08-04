package com.dtflys.forest.exceptions;

public class ForestInterceptorDefineException extends ForestRuntimeException {
    public ForestInterceptorDefineException(Class clazz) {
        super("[Forest] Interceptor class \"" + clazz.getName() + "\" cannot be initialized, because interceptor class must implements com.dtflys.forest.interceptor.Interceptor");
    }
}
