package com.dtflys.forest.reflection;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.utils.ForestProgress;

import java.lang.annotation.Annotation;

public interface MetaRequestLifeCycle<A extends Annotation, I> extends Interceptor<I> {

    default MetaRequest buildMetaRequest(A annotation) {
        return null;
    }

    @Override
    default void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    default void onSuccess(I data, ForestRequest request, ForestResponse response) {

    }
}
