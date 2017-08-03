package org.forest.executors.httpclient.response;

import org.apache.http.HttpResponse;
import org.forest.exceptions.ForestNetworkException;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestFuture;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.ForestResponseFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Future;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public class HttpclientResponseHandler {

    protected final ForestRequest request;

    protected final ResponseHandler responseHandler;

    public HttpclientResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        this.request = request;
        this.responseHandler = responseHandler;
    }

    public void handle(HttpResponse httpResponse, ForestResponse response) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        responseHandler.handleSync(request, response);
        if (response.isError()) {
            throw new ForestNetworkException(
                    httpResponse.getStatusLine().getReasonPhrase(), statusCode, response);
        }
    }


    public void handleSuccess(ForestResponse response) {
        Class onSuccessGenericType = responseHandler.getOnSuccessClassGenericType();
        Object resultData = responseHandler.handleResultType(request, response, onSuccessGenericType, onSuccessGenericType);
        responseHandler.handleSuccess(resultData, request, response);
    }

    public void handleError(ForestResponse response) {
        responseHandler.handleError(request, response);
    }

    public void handleError(ForestResponse response, Exception ex) {
        responseHandler.handleError(request, response, ex);
    }


    public void handleFuture(
                     final Future<HttpResponse> httpResponseFuture,
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


    private  <T> void handleFutureResult(
            final Future<HttpResponse> httpResponseFuture,
            final Class<T> innerType,
            final ForestResponseFactory forestResponseFactory) {
        ForestFuture<T> future = new ForestFuture<>(
                request, innerType, responseHandler, httpResponseFuture, forestResponseFactory);
        responseHandler.handleResult(future);
    }


}
