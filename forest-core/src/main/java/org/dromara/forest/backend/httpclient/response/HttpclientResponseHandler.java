package org.dromara.forest.backend.httpclient.response;

import org.dromara.forest.backend.ResponseHandler;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.http.ForestResponseFactory;
import org.apache.http.HttpResponse;

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
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String msg = httpResponse.getStatusLine().getReasonPhrase();
        handleSync(response, statusCode, msg);
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
