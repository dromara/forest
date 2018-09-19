package com.dtflys.forest.backend.httpclient.response;

import com.dtflys.forest.backend.BackbendResponseHandler;
import com.dtflys.forest.handler.ResponseHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import org.apache.http.HttpResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Future;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public class HttpclientResponseHandler extends BackbendResponseHandler<HttpResponse> {

    public HttpclientResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        super(request, responseHandler);
    }

    public void handleSync(HttpResponse httpResponse, ForestResponse response) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String msg = httpResponse.getStatusLine().getReasonPhrase();
        handleSync(response, statusCode, msg);
    }


    public void handleFuture(
            final Future httpResponseFuture,
            ForestResponseFactory forestResponseFactory) {
        Type returnType = responseHandler.getReturnType();
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
        handleFutureResult(httpResponseFuture, paramClass, forestResponseFactory);
    }


    protected void handleFutureResult(Future httpResponseFuture, Class innerType, ForestResponseFactory forestResponseFactory) {
        HttpclientForestFuture<HttpResponse, HttpResponse> future = new HttpclientForestFuture<>(
                request, innerType, responseHandler, httpResponseFuture, forestResponseFactory);
        responseHandler.handleResult(future);
    }

}
