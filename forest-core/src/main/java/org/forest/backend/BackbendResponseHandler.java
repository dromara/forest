package org.forest.backend;


import org.forest.exceptions.ForestNetworkException;
import org.forest.handler.ResponseHandler;
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
public abstract class BackbendResponseHandler<R> {

    protected final ForestRequest request;

    protected final ResponseHandler responseHandler;

    public BackbendResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        this.request = request;
        this.responseHandler = responseHandler;
    }


    public Object handleSync(ForestResponse response, int statusCode, String msg) {
        Object result = responseHandler.handleSync(request, response);
        if (response.isError()) {
            throw new ForestNetworkException(
                    msg, statusCode, response);
        }
        return result;
    }


    public Object handleSuccess(ForestResponse response) {
        Class onSuccessGenericType = responseHandler.getOnSuccessClassGenericType();
        Object resultData = responseHandler.handleResultType(request, response, onSuccessGenericType, onSuccessGenericType);
        return responseHandler.handleSuccess(resultData, request, response);
    }


    public void handleError(ForestResponse response) {
        responseHandler.handleError(request, response);
    }

    public void handleError(ForestResponse response, Exception ex) {
        responseHandler.handleError(request, response, ex);
    }


    public abstract void handleFuture(
                     final Future<R> httpResponseFuture,
                     ForestResponseFactory forestResponseFactory);



}
