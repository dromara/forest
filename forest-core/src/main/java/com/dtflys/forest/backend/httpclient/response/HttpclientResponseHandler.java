package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.backend.AbstractBackendResponseHandler;
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
public class HttpclientResponseHandler extends AbstractBackendResponseHandler<HttpResponse> {

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
            final Future httpResponseFuture,
            Date requestTime,
            ForestResponseFactory forestResponseFactory) {
        Type returnType = lifeCycleHandler.getResultType();
        Type paramType;
        Class paramClass = null;
        if (returnType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) returnType).getRawType();
            if (!Future.class.isAssignableFrom((Class<?>) rawType)) {
                return;
            }
            paramType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            if (paramType == null) {
                paramType = Object.class;
            }
            paramClass = (Class) paramType;
        }
        else if (returnType instanceof Class) {
            if (!Future.class.isAssignableFrom((Class<?>) returnType)) {
                return;
            }
            paramClass = Object.class;
        }
        handleFutureResult(httpResponseFuture, requestTime, paramClass, forestResponseFactory);
    }


    protected void handleFutureResult(Future httpResponseFuture, Date requestTime, Class innerType, ForestResponseFactory forestResponseFactory) {
        HttpclientForestFuture<HttpResponse, HttpResponse> future = new HttpclientForestFuture<>(
                request, requestTime, innerType, lifeCycleHandler, httpResponseFuture, forestResponseFactory);
        lifeCycleHandler.handleFuture(future);
    }

}
