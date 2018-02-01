package org.forest.reflection;

import org.apache.http.HttpResponse;
import org.forest.callback.OnSuccess;
import org.forest.exceptions.ForestNetworkException;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.handler.DefaultResultHandler;
import org.forest.handler.ResponseHandler;
import org.forest.handler.ResultHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 17:00
 */
public class MethodResponseHandler<T> implements ResponseHandler {

    private final ForestMethod method;

    private final Type returnType;

    private final Class returnClass;

    private final Class onSuccessClassGenericType;

    private final ResultHandler resultHandler = new DefaultResultHandler();

    private volatile T resultData;

    public MethodResponseHandler(ForestMethod method, Class onSuccessClassGenericType) {
        this.method = method;
        this.onSuccessClassGenericType = onSuccessClassGenericType;
        this.returnType = method.getReturnType();
        this.returnClass = method.getReturnClass();
    }

    @Override
    public void handleSync(ForestRequest request, ForestResponse response) {
        handleSyncWitchException(request, response, null);
    }

    @Override
    public void handleSyncWitchException(ForestRequest request, ForestResponse response, Exception ex) {
        try {
            Object resultData = handleResultType(request, response, returnType, returnClass);
            if (response.isSuccess()) {
                resultData = handleSuccess(resultData, request, response);
            } else {
                if (ex != null) {
                    handleError(request, response, ex);
                }
                else {
                    handleError(request, response);
                }
            }
            handleResult(resultData);
        } catch (Throwable e) {
            throw e;
        } finally {
            request.getInterceptorChain().afterExecute(request, response);
        }
    }

    @Override
    public Object handleResultType(ForestRequest request, ForestResponse response) {
        return handleResultType(request, response, returnType, returnClass);
    }


    @Override
    public synchronized Object handleResultType(ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        Object resultData = resultHandler.getResult(request, response, resultType, resultClass);
        response.setResult(resultData);
        this.resultData = (T) resultData;
        return resultData;
    }

    @Override
    public Object handleSuccess(Object resultData, ForestRequest request, ForestResponse response) {
        request.getInterceptorChain().onSuccess(resultData, request, response);
        OnSuccess onSuccess = request.getOnSuccess();
        if (onSuccess != null) {
            resultData = resultHandler.getResult(request, response, onSuccessClassGenericType, onSuccessClassGenericType);
            onSuccess.onSuccess(resultData, request, response);
        }
        resultData = response.getResult();
        return resultData;
    }

    @Override
    public void handleError(ForestRequest request, ForestResponse response) {
        ForestNetworkException networkException = new ForestNetworkException("", response.getStatusCode(), response);
        handleError(request, response, networkException);
    }

    @Override
    public void handleError(ForestRequest request, ForestResponse response, Exception ex) {
        ForestRuntimeException e = null;
        if (ex instanceof ForestRuntimeException) {
            e = (ForestRuntimeException) ex;
        }
        else {
            e = new ForestRuntimeException(ex);
        }
        request.getInterceptorChain().onError(e, request, response);
        if (request.getOnError() != null) {
            request.getOnError().onError(e, request, response);
        }
        else {
            throw e;
        }
    }

    @Override
    public Object handleResult(Object resultData) {
        this.resultData = (T) resultData;
        return resultData;
    }


    @Override
    public Type getReturnType() {
        return returnType;
    }

    public T getResultData() {
        return resultData;
    }

    @Override
    public Class getOnSuccessClassGenericType() {
        return onSuccessClassGenericType;
    }
}
