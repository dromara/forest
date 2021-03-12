package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.AbstractBackendResponseHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import okhttp3.Response;
import com.dtflys.forest.handler.LifeCycleHandler;

import java.util.Date;
import java.util.concurrent.Future;


/**
 * OkHttp3后端请求响应处理器
 *
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 1.1.0
 */
public class OkHttp3ResponseHandler extends AbstractBackendResponseHandler<Object> {

    public OkHttp3ResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        super(request, lifeCycleHandler);
    }

    public Object handleSync(Response okResponse, ForestResponse response) {
        int statusCode = okResponse.code();
        String msg = okResponse.message();
        return handleSync(response, statusCode, msg);
    }

    @Override
    public void handleFuture(Future<Object> httpResponseFuture, Date requestTime, ForestResponseFactory forestResponseFactory) {
        lifeCycleHandler.handleResult(httpResponseFuture);
    }


}
