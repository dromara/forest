package com.dtflys.forest.result;

import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

public class FutureResultHandler extends AfterExecuteResultTypeHandler<Future> {

    public FutureResultHandler() {
        super(Future.class);
    }

    @Override
    public boolean matchType(Type resultType, Class resultClass) {
        if (Future.class.isAssignableFrom(resultClass) && resultType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) resultType;
            final Class<?> rowClass = (Class<?>) parameterizedType.getRawType();
            return Future.class.isAssignableFrom(rowClass);
        }
        return false;
    }

    @Override
    public Object getResult(ResultHandler resultHandler, ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        final ParameterizedType parameterizedType = (ParameterizedType) resultType;
        final Type realType = parameterizedType.getActualTypeArguments()[0];
        final Class<?> realClass = ReflectUtils.toClass(parameterizedType.getActualTypeArguments()[0]);
        if (realClass == null) {
            return ((MethodLifeCycleHandler<?>) request.getLifeCycleHandler()).getResultData();
        }
        return resultHandler.getResult(null, request, response, realType, realClass);
    }
}
