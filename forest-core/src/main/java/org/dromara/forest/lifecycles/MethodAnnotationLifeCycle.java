package org.dromara.forest.lifecycles;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;
import org.dromara.forest.reflection.ForestMethod;

import java.lang.annotation.Annotation;

/**
 * 方法注解的生命周期
 * @param <A> 注解类
 * @param <I> 返回类型
 */
public interface MethodAnnotationLifeCycle<A extends Annotation, I> extends Interceptor<I> {

    void onMethodInitialized(ForestMethod method, A annotation);

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    default void onSuccess(I data, ForestRequest request, ForestResponse response) {

    }
}
