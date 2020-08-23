package com.dtflys.forest.lifecycles;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.mapping.MappingParameter;
import com.dtflys.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

/**
 * 参数注解的生命周期
 * @param <A> 注解类
 * @param <I> 返回类型
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 0:22
 */
public interface ParameterAnnotationLifeCycle<A extends Annotation, I> extends Interceptor<I> {

    /**
     * 在被注解修饰的方法参数初始化时被调用
     * @param method
     * @param parameter
     * @param annotation
     */
    default void onParameterInitialized(ForestMethod method, MappingParameter parameter, A annotation) {
    }

    @Override
    default void onSuccess(I data, ForestRequest request, ForestResponse response) {

    }

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }
}
