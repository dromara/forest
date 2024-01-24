package com.dtflys.forest.backend;


import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ResultGetter;

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


    public ResultGetter handleSync(ForestResponse response) {
        if (request.isAutoRedirection() && response.isRedirection()) {
            // 进行重定向
            final ForestRequest redirectionRequest = response.redirectionRequest();
            return redirectionRequest.execute(request.getBackend(), lifeCycleHandler);
        }
        lifeCycleHandler.handleSync(request, response);
        return response;
    }

    public void handleError(ForestResponse response, Throwable ex) {
        lifeCycleHandler.handleError(request, response, ex);
    }


    public abstract void handleFuture(final ForestRequest request, final Future<R> httpResponseFuture);



}
