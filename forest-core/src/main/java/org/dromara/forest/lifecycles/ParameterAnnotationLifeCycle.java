package org.dromara.forest.lifecycles;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;
import org.dromara.forest.mapping.MappingParameter;
import org.dromara.forest.reflection.ForestMethod;

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
     * @param method {@link ForestMethod}对象
     * @param parameter {@link MappingParameter}对象
     * @param annotation 该生命周期类所绑定的注解对象
     */
    void onParameterInitialized(ForestMethod method, MappingParameter parameter, A annotation);

    @Override
    default void onSuccess(I data, ForestRequest request, ForestResponse response) {

    }

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }
}
