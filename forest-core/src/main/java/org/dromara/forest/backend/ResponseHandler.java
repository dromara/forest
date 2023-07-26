package org.dromara.forest.backend;


import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.handler.LifeCycleHandler;

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
            return redirectionRequest.execute(request.getBackend(), lifeCycleHandler);
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
