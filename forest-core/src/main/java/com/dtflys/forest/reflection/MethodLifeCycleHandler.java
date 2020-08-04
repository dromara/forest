package com.dtflys.forest.reflection;

import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.Type;

/**
 * 请求方法生命周期处理器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 17:00
 */
public class MethodLifeCycleHandler<T> implements LifeCycleHandler {

    private final Type returnType;

    private final Class returnClass;

    private final Type onSuccessClassGenericType;

    private static final ResultHandler resultHandler = new ResultHandler();

    private volatile T resultData;

    public MethodLifeCycleHandler(ForestMethod method, Type onSuccessClassGenericType) {
        this.onSuccessClassGenericType = onSuccessClassGenericType;
        this.returnType = method.getReturnType();
        this.returnClass = method.getReturnClass();
    }

    @Override
    public Object handleSync(ForestRequest request, ForestResponse response) {
        return handleSyncWitchException(request, response, null);
    }

    @Override
    public Object handleSyncWitchException(ForestRequest request, ForestResponse response, Exception ex) {
        try {
            Object resultData = handleResultType(request, response, returnType, returnClass);
            if (resultData instanceof ForestResponse) {
                handleResult(resultData);
                return resultData;
            }
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
            return resultData;
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
        if (!(resultData instanceof ForestResponse)) {
            response.setResult(resultData);
        }
        this.resultData = (T) resultData;
        return resultData;
    }



    @Override
    public Object handleSuccess(Object resultData, ForestRequest request, ForestResponse response) {
        request.getInterceptorChain().onSuccess(resultData, request, response);
        OnSuccess onSuccess = request.getOnSuccess();
        if (onSuccess != null) {
            resultData = resultHandler.getResult(request, response, onSuccessClassGenericType, ReflectUtils.getClassByType(onSuccessClassGenericType));
            onSuccess.onSuccess(resultData, request, response);
        }
        resultData = response.getResult();
        return resultData;
    }

    @Override
    public void handleInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        request.getInterceptorChain().onInvokeMethod(request, method, args);
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
    public void handleProgress(ForestRequest request, ForestProgress progress) {
//        request.getInterceptorChain().onProgress(progress);
        OnProgress onProgress = request.getOnProgress();
        if (onProgress != null) {
            onProgress.onProgress(progress);
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
    public Type getOnSuccessClassGenericType() {
        return onSuccessClassGenericType;
    }
}
