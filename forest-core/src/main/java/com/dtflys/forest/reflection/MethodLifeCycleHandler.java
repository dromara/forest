package com.dtflys.forest.reflection;

import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ResponseResult;
import com.dtflys.forest.interceptor.ResponseResultStatus;
import com.dtflys.forest.interceptor.ResponseSuccess;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * 请求方法生命周期处理器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 17:00
 */
public class MethodLifeCycleHandler<T> implements LifeCycleHandler {

    private Type resultType;

    private Class resultRawClass;

    private Type onSuccessClassGenericType;

    private static final ResultHandler RESULT_HANDLER = new ResultHandler();

    private volatile ForestResponse<T> response;

    private volatile T resultData;


    public MethodLifeCycleHandler(final Type resultType, final Type onSuccessClassGenericType) {
        this.onSuccessClassGenericType = onSuccessClassGenericType;
        this.resultType = ReflectUtils.toType(resultType);
        this.resultRawClass = ReflectUtils.toClass(resultType);
    }


    @Override
    public Object handleSync(final ForestRequest request, final ForestResponse response) {
        return handleSyncWithException(request, response, null);
    }

    @Override
    public Object handleSyncWithException(final ForestRequest request, final ForestResponse response, final Throwable ex) {
        this.response = response;
        try {
            Object resultData = null;
            final ResponseResult responseResult = handleResponse(request, response);
            final ResponseSuccess responseSuccess = responseResult instanceof ResponseSuccess ? (ResponseSuccess) responseResult : null;
            ResponseResultStatus status = responseResult.getStatus();
            if (ResponseResultStatus.PROCEED == status) {
                status = response.isSuccess() ? ResponseResultStatus.SUCCESS : ResponseResultStatus.ERROR;
            }
            if (ResponseResultStatus.SUCCESS == status) {
                final Optional<?> resultOpt = responseSuccess != null ? responseSuccess.getResult() : null;
                resultData = handleResultType(resultOpt, request, response, resultType, resultRawClass);
                handleSuccess(resultData, resultOpt, request, response);
                if ((!ForestResponse.class.isAssignableFrom(resultRawClass)
                        && !Future.class.isAssignableFrom(resultRawClass))
                        || request.isDownloadFile()) {
                    resultData = response.getResult();
                }
            } else if (ResponseResultStatus.ERROR == status) {
                if (ex != null) {
                    resultData = handleError(request, response, ex);
                } else {
                    resultData = handleError(request, response);
                }
            }
            handleResult(resultData);
            if (ForestResponse.class.isAssignableFrom(resultRawClass)) {
                if (!(resultData instanceof ForestResponse)) {
                    response.setResult(resultData);
                    resultData = response;
                }
                handleResult(resultData);
                return resultData;
            }
            return resultData;
        } catch (Throwable th) {
            Object resultData = response.getResult();
            handleResult(resultData);
            if (ForestResponse.class.isAssignableFrom(resultRawClass)) {
                if (!(resultData instanceof ForestResponse)) {
                    response.setResult(resultData);
                    resultData = response;
                }
                handleResult(resultData);
                return resultData;
            } else {
                throw th;
            }
        } finally {
            request.getInterceptorChain().afterExecute(request, response);
        }
    }

    @Override
    public Object handleResultType(final ForestRequest request, final ForestResponse response) {
        return handleResultType(request, response, resultType, resultRawClass);
    }

    @Override
    public Object handleResultType(Optional<?> resultOpt, ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        this.response = response;
        final Object resultData = RESULT_HANDLER.getResult(resultOpt, request, response, resultType, resultClass);
        if (!(resultData instanceof ForestResponse)) {
            response.setResult(resultData);
        }
        this.resultData = (T) resultData;
        return resultData;
    }


    @Override
    public synchronized Object handleResultType(final ForestRequest request, final ForestResponse response, final Type resultType, final Class resultClass) {
        this.response = response;
        final Object resultData = RESULT_HANDLER.getResult(null, request, response, resultType, resultClass);
        if (!(resultData instanceof ForestResponse)) {
            response.setResult(resultData);
        }
        this.resultData = (T) resultData;
        return resultData;
    }

    private void handleSaveCookie(final ForestRequest request, final ForestResponse response) {
        final List<ForestCookie> cookieList = response.getCookies();
        if (cookieList != null && cookieList.size() > 0) {
            final ForestCookies cookies = new ForestCookies(response.getCookies());
            handleSaveCookie(request, cookies);
        }
    }

    @Override
    public ResponseResult handleResponse(ForestRequest request, ForestResponse response) {
        this.response = response;
        handleSaveCookie(request, response);
        return request.getInterceptorChain().onResponse(request, response);
    }


    @Override
    public void handleSuccess(final Object resultData, final Optional<?> resultOpt, final ForestRequest request, final ForestResponse response) {
        request.getInterceptorChain().onSuccess(resultData, request, response);
        final OnSuccess onSuccess = request.getOnSuccess();
        if (onSuccess != null) {
            final Object result = RESULT_HANDLER.getResult(
                    resultOpt, request, response, onSuccessClassGenericType, ReflectUtils.toClass(onSuccessClassGenericType));
            onSuccess.onSuccess(result, request, response);
        }
    }

    @Override
    public void handleInvokeMethod(final ForestRequest request, final ForestMethod method, final Object[] args) {
        request.getInterceptorChain().onInvokeMethod(request, method, args);
    }

    @Override
    public Object handleError(final ForestRequest request, final ForestResponse response) {
        handleSaveCookie(request, response);
        final ForestNetworkException networkException = new ForestNetworkException(
                "", response.getStatusCode(), response);
        return handleError(request, response, networkException);
    }

    @Override
    public Object handleError(final ForestRequest request, final ForestResponse response, final Throwable ex) {
        this.response = response;
        handleSaveCookie(request, response);
        ForestRuntimeException e = null;
        if (ex instanceof ForestRuntimeException) {
            e = (ForestRuntimeException) ex;
        }
        else {
            e = new ForestRuntimeException(ex);
        }
        request.getInterceptorChain().onError(e, request, response);
        Object resultData = null;
        if (request.getOnError() != null) {
            request.getOnError().onError(e, request, response);
            resultData = response.getResult();
            return resultData;
        }
        else {
            throw e;
        }
    }

    @Override
    public byte[] handleBodyEncode(final ForestRequest request, final ForestEncoder encoder, final byte[] encodedData) {
        return request.getInterceptorChain().onBodyEncode(request, encoder, encodedData);
    }

    @Override
    public void handleCanceled(final ForestRequest request, final ForestResponse response) {
        this.response = response;
        request.getInterceptorChain().onCanceled(request, response);
        if (request.getOnCanceled() != null) {
            request.getOnCanceled().onCanceled(request, response);
        }
    }

    @Override
    public void handleProgress(final ForestRequest request, final ForestProgress progress) {
        request.getInterceptorChain().onProgress(progress);
        final OnProgress onProgress = request.getOnProgress();
        if (onProgress != null) {
            onProgress.onProgress(progress);
        }
    }

    @Override
    public void handleLoadCookie(final ForestRequest request, final ForestCookies cookies) {
        request.getInterceptorChain().onLoadCookie(request, cookies);
        final OnLoadCookie onLoadCookie = request.getOnLoadCookie();
        if (onLoadCookie != null) {
            onLoadCookie.onLoadCookie(request, cookies);
        }
    }

    @Override
    public void handleSaveCookie(final ForestRequest request, final ForestCookies cookies) {
        request.getInterceptorChain().onSaveCookie(request, cookies);
        final OnSaveCookie onSaveCookie = request.getOnSaveCookie();
        if (onSaveCookie != null) {
            onSaveCookie.onSaveCookie(request, cookies);
        }
    }

    @Override
    public Object handleResult(final Object resultData) {
        this.resultData = (T) resultData;
        return resultData;
    }

    @Override
    public Object handleFuture(final ForestRequest request, final Future resultData) {
        if (resultData == null) {
            return null;
        }
        if (Future.class.isAssignableFrom(resultRawClass)) {
            this.resultData = (T) resultData;
            return resultData;
        }
        return null;
    }

    public ForestResponse<T> getResponse() {
        return response;
    }

    @Override
    public Type getResultType() {
        return resultType;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultType(final Type resultType) {
        this.resultType = ReflectUtils.toType(resultType);
        this.resultRawClass = ReflectUtils.toClass(resultType);
    }

    public void setResultRawClass(final Class resultRawClass) {
        this.resultRawClass = resultRawClass;
    }

    public void setOnSuccessClassGenericType(final Type onSuccessClassGenericType) {
        this.onSuccessClassGenericType = onSuccessClassGenericType;
    }

    @Override
    public Type getOnSuccessClassGenericType() {
        return onSuccessClassGenericType;
    }
}
