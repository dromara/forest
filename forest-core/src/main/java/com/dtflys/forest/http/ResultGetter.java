package com.dtflys.forest.http;

import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.utils.ReflectUtils;
import com.dtflys.forest.utils.TypeReference;

import java.lang.reflect.Type;
import java.util.concurrent.Future;

public abstract class ResultGetter {

    protected final static ResultHandler HANDLER = new ResultHandler();

    protected final ForestRequest request;

    protected ResultGetter(ForestRequest request) {
        this.request = request;
    }

    protected abstract ForestResponse getResponse();


    public <T> T result(Class<T> clazz) {
        final Object result = HANDLER.getResult(request, getResponse(), clazz);
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    public <T> T result(Type type) {
        final Class resultClass = ReflectUtils.toClass(type);
        if (ForestFuture.class.isAssignableFrom(resultClass) && this instanceof ForestFuture) {
            return (T) this;
        }
        if (Future.class.isAssignableFrom(resultClass) && this instanceof Future) {
            return (T) this;
        }
        final Object result = HANDLER.getResult(request, getResponse(), type);
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    public <T> T result(TypeReference<T> typeReference) {
        return result(typeReference.getType());
    }
}
