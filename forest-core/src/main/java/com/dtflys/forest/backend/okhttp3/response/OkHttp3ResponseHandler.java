package com.dtflys.forest.backend.okhttp3.response;

import com.dtflys.forest.backend.BackendResponseHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import okhttp3.Response;
import com.dtflys.forest.handler.LifeCycleHandler;

import java.util.concurrent.Future;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public class OkHttp3ResponseHandler extends BackendResponseHandler<Object> {

    public OkHttp3ResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        super(request, lifeCycleHandler);
    }

    public Object handleSync(Response okResponse, ForestResponse response) {
        int statusCode = okResponse.code();
        String msg = okResponse.message();
        return handleSync(response, statusCode, msg);
    }

    @Override
    public void handleFuture(Future<Object> httpResponseFuture, ForestResponseFactory forestResponseFactory) {
        lifeCycleHandler.handleResult(httpResponseFuture);
    }


}
