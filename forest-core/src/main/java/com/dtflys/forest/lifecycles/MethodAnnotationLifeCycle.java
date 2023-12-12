package com.dtflys.forest.lifecycles;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestProgress;

import java.lang.annotation.Annotation;

/**
 * 方法注解的生命周期
 * @param <A> 注解类
 * @param <I> 返回类型
 */
public interface MethodAnnotationLifeCycle<A extends Annotation> extends Interceptor {

    void onMethodInitialized(ForestMethod method, A annotation);

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    default void onSuccess(ForestRequest request, ForestResponse response) {

    }
}
