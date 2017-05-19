package org.forest.reflection;

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
        ResultHandler resultHandler = new DefaultResultHandlerAdaptor();
        Type returnType = method.getReturnType();
        Class resultType = method.getResultType();
        Object resultData = resultHandler.getResult(request, response, returnType);
        response.setResult(resultData);
        if (response.isSuccess()) {
            Object data = resultData;
            request.getInterceptorChain().onSuccess(data, request, response);
            data = resultData = response.getResult();
            OnSuccess onSuccess = request.getOnSuccess();
            if (onSuccess != null) {
                if (onSuccessClassGenericType != null) {
                    data = resultHandler.getResult(request, response, onSuccessClassGenericType);
                }
                else if (void.class.isAssignableFrom(resultType)) {
                    data = resultHandler.getResult(request, response, String.class);
                }
                onSuccess.onSuccess(data, request, response);
            }
        }
        else {
            if (request.getOnError() != null) {
                ForestNetworkException networkException = new ForestNetworkException("", response.getStatusCode());
                ForestRuntimeException e = new ForestRuntimeException(networkException);
                request.getInterceptorChain().onError(e, request, response);
                request.getOnError().onError(e, request);
            }
        }
        this.resultData = (T) resultData;
    }

    public T getResultData() {
        return resultData;
    }
}
