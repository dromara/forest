package com.dtflys.forest.backend;


import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.handler.ResponseHandler;

import java.util.concurrent.Future;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 15:46
 */
public abstract class BackendResponseHandler<R> {

    protected final ForestRequest request;

    protected final ResponseHandler responseHandler;

    public BackendResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
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
