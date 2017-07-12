package org.forest.reflection;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.forest.callback.OnSuccess;
import org.forest.exceptions.ForestNetworkException;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.handler.DefaultResultHandlerAdaptor;
import org.forest.handler.ResponseHandler;
import org.forest.handler.ResultHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

import java.lang.reflect.Type;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 17:00
 */
public class MethodResponseHandler<T> implements ResponseHandler {

    private final ForestMethod method;

    private final Class onSuccessClassGenericType;

    private T resultData;

    public MethodResponseHandler(ForestMethod method, Class onSuccessClassGenericType) {
        this.method = method;
        this.onSuccessClassGenericType = onSuccessClassGenericType;
    }

    @Override
    public void handle(ForestRequest request, ForestResponse response) {
        try {
            ResultHandler resultHandler = new DefaultResultHandlerAdaptor();
            Type returnType = method.getReturnType();
            Class returnClass = method.getReturnClass();
            Object resultData = resultHandler.getResult(request, response, returnType, returnClass);
            response.setResult(resultData);
            if (response.isSuccess()) {
                request.getInterceptorChain().onSuccess(resultData, request, response);
                OnSuccess onSuccess = request.getOnSuccess();
                if (onSuccess != null) {
                    if (onSuccessClassGenericType != null) {
                        resultData = resultHandler.getResult(request, response, onSuccessClassGenericType, onSuccessClassGenericType);
                    } else if (void.class.isAssignableFrom(returnClass)) {
                        resultData = resultHandler.getResult(request, response, String.class, String.class);
                    }
                    onSuccess.onSuccess(resultData, request, response);
                }
                resultData = response.getResult();
            } else {
                ForestNetworkException networkException = new ForestNetworkException("", response.getStatusCode(), response);
                ForestRuntimeException e = new ForestRuntimeException(networkException);
                request.getInterceptorChain().onError(e, request, response);
                if (request.getOnError() != null) {
                    request.getOnError().onError(e, request);
                }
                else {
                    throw e;
                }
            }
            this.resultData = (T) resultData;
        } catch (Throwable e) {
            throw e;
        } finally {
            request.getInterceptorChain().afterExecute(request, response);
        }
    }

    public T getResultData() {
        return resultData;
    }
}
