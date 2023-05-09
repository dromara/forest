package org.dromara.forest.backend.okhttp3.response;

import org.dromara.forest.backend.ResponseHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import okhttp3.Response;
import org.dromara.forest.handler.LifeCycleHandler;

import java.util.concurrent.Future;


/**
 * OkHttp3后端请求响应处理器
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.1.0
 */
public class OkHttp3ResponseHandler extends ResponseHandler<Object> {

    public OkHttp3ResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        super(request, lifeCycleHandler);
    }

    public Object handleSync(Response okResponse, ForestResponse response) {
        int statusCode = okResponse.code();
        String msg = okResponse.message();
        return handleSync(response, statusCode, msg);
    }

    @Override
    public void handleFuture(final ForestRequest request, Future<Object> httpResponseFuture) {
        lifeCycleHandler.handleFuture(request, httpResponseFuture);
    }


}