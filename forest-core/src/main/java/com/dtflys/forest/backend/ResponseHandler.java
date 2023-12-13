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
public abstract class ResponseHandler<R> {

    protected final ForestRequest request;

    protected final LifeCycleHandler lifeCycleHandler;

    public ResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        this.request = request;
        this.lifeCycleHandler = lifeCycleHandler;
    }


    public Object handleSync(ForestResponse response, int statusCode, String msg) {
        if (request.isAutoRedirection() && response.isRedirection()) {
            // 进行重定向
            final ForestRequest redirectionRequest = response.redirectionRequest();
            return redirectionRequest.asObject(request.getBackend(), lifeCycleHandler);
        }
        final Object result = lifeCycleHandler.handleSync(request, response);
        if (result instanceof ForestResponse) {
            return result;
        }
        return result;
    }

    public void handleError(ForestResponse response, Throwable ex) {
        lifeCycleHandler.handleError(request, response, ex);
    }


    public abstract void handleFuture(final ForestRequest request, final Future<R> httpResponseFuture);



}
