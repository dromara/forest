package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.backend.ResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.http.HttpResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.Future;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public class HttpclientResponseHandler extends ResponseHandler<HttpResponse> {

    public HttpclientResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        super(request, lifeCycleHandler);
    }

    public void handleSync(HttpResponse httpResponse, ForestResponse response) {
        handleSync(response);
    }


    @Override
    public void handleFuture(
            final ForestRequest request,
            final Future httpResponseFuture) {
        lifeCycleHandler.handleFuture(request, httpResponseFuture);
    }


    protected void handleFutureResult(Future httpResponseFuture, Date requestTime, Class innerType, ForestResponseFactory forestResponseFactory) {
        final HttpclientForestFuture<HttpResponse, HttpResponse> future = new HttpclientForestFuture<>(
                request, requestTime, innerType, lifeCycleHandler, httpResponseFuture, forestResponseFactory);
        lifeCycleHandler.handleFuture(request, future);
    }

}
