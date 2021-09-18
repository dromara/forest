package com.dtflys.forest.backend;


import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.Future;


/**
 * 后端请求响应处理器抽象基类
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.3.0
 */
public abstract class AbstractBackendResponseHandler<R> {

    protected final ForestRequest request;

    protected final LifeCycleHandler lifeCycleHandler;

    public AbstractBackendResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        this.request = request;
        this.lifeCycleHandler = lifeCycleHandler;
    }


    public Object handleSync(ForestResponse response, int statusCode, String msg) {
        Object result = lifeCycleHandler.handleSync(request, response);
        if (result instanceof ForestResponse) {
            return result;
        }
        return result;
    }


    public Object handleSuccess(ForestResponse response) {
        Type onSuccessGenericType = lifeCycleHandler.getOnSuccessClassGenericType();
        Object resultData = lifeCycleHandler.handleResultType(request, response, onSuccessGenericType, ReflectUtils.toClass(onSuccessGenericType));
        return lifeCycleHandler.handleSuccess(resultData, request, response);
    }


    public void handleError(ForestResponse response) {
        lifeCycleHandler.handleError(request, response);
    }

    public void handleError(ForestResponse response, Throwable ex) {
        lifeCycleHandler.handleError(request, response, ex);
    }


    public abstract void handleFuture(
                     final Future<R> httpResponseFuture,
                     Date requestTime,
                     ForestResponseFactory forestResponseFactory);



}
