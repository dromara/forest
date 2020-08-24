package com.dtflys.forest.lifecycles;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.proxy.InterfaceProxyHandler;

import java.lang.annotation.Annotation;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-23 23:04
 */
public interface BaseAnnotationLifeCycle <A extends Annotation, I> extends Interceptor<I> {

    /**
     * 在被注解修饰的接口初始化时被调用
     * @param method
     * @param parameter
     * @param annotation
     */
    default void onProxyHandlerInitialized(InterfaceProxyHandler interfaceProxyHandler, A annotation) {
    }

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
    }

    @Override
    default void onSuccess(I data, ForestRequest request, ForestResponse response) {
    }
}
