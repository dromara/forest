package com.dtflys.forest.http;

import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.utils.TypeReference;

import java.lang.reflect.Type;

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
        final Object result = HANDLER.getResult(request, getResponse(), type);
        if (result == null) {
            return null;
        }
        return (T) result;
    }

    public <T> T result(TypeReference<T> typeReference) {
        final Object result = HANDLER.getResult(request, getResponse(), typeReference.getType());
        if (result == null) {
            return null;
        }
        return (T) result;
    }
}
