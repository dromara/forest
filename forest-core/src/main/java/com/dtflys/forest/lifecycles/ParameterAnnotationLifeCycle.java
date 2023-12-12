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
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-21 0:22
 */
public interface ParameterAnnotationLifeCycle<A extends Annotation> extends Interceptor {

    /**
     * 在被注解修饰的方法参数初始化时被调用
     * @param method {@link ForestMethod}对象
     * @param parameter {@link MappingParameter}对象
     * @param annotation 该生命周期类所绑定的注解对象
     */
    void onParameterInitialized(ForestMethod method, MappingParameter parameter, A annotation);

    @Override
    default void onSuccess(ForestRequest request, ForestResponse response) {
    }

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
    }
}
