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
public interface BaseAnnotationLifeCycle <A extends Annotation> extends Interceptor {

    /**
     * 在被注解修饰的接口初始化时被调用
     * @param interfaceProxyHandler 请求接口动态代理处理器
     * @param annotation 该生命周期类所绑定的注解
     */
    default void onProxyHandlerInitialized(InterfaceProxyHandler interfaceProxyHandler, A annotation) {
    }

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
    }

    @Override
    default void onSuccess(ForestRequest request, ForestResponse response) {
    }
}
