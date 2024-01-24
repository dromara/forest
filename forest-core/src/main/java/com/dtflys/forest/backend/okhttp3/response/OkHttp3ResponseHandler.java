package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.ResponseHandler;
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
public class OkHttp3ResponseHandler extends ResponseHandler<Object> {

    public OkHttp3ResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        super(request, lifeCycleHandler);
    }

    public Object handleSync(Response okResponse, ForestResponse response) {
        return handleSync(response);
    }

    @Override
    public void handleFuture(final ForestRequest request, Future<Object> httpResponseFuture) {
        lifeCycleHandler.handleFuture(request, httpResponseFuture);
    }


}
