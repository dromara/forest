package com.dtflys.forest.reflection;

import com.dtflys.forest.callback.OnLoadCookie;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.handler.ResultHandler;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ForestProgress;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.Type;
import java.util.List;
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


    public MethodLifeCycleHandler(Type resultType, Type onSuccessClassGenericType) {
        this.onSuccessClassGenericType = onSuccessClassGenericType;
        this.resultType = ReflectUtils.toType(resultType);
        this.resultRawClass = ReflectUtils.toClass(resultType);
    }


    @Override
    public Object handleSync(ForestRequest request, ForestResponse response) {
        return handleSyncWithException(request, response, null);
    }

    @Override
    public Object handleSyncWithException(ForestRequest request, ForestResponse response, Throwable ex) {
        this.response = response;
        try {
            Object resultData = null;
            if (response.isSuccess()) {
                resultData = handleResultType(request, response, resultType, resultRawClass);
                handleSuccess(resultData, request, response);
                if ((!ForestResponse.class.isAssignableFrom(resultRawClass)
                        && !Future.class.isAssignableFrom(resultRawClass))
                        || request.isDownloadFile()) {
                    resultData = response.result();
                }
            } else {
                if (ex != null) {
                    handleError(request, response, ex);
                } else {
                    handleError(request, response);
                }
            }
//            handleResult(resultData);
            if (ForestResponse.class.isAssignableFrom(resultRawClass)) {
                if (!(resultData instanceof ForestResponse)) {
                    response.setResult(resultData);
                    resultData = response;
                }
//                handleResult(resultData);
                return resultData;
            }
            return resultData;
        } catch (Throwable th) {
            Object resultData = response.result();
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
    public Object handleResultType(ForestRequest request, ForestResponse response) {
        return handleResultType(request, response, resultType, resultRawClass);
    }


    @Override
    public synchronized Object handleResultType(ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        this.response = response;
        Object resultData = RESULT_HANDLER.getResult(request, response, resultType, resultClass);
        if (!(resultData instanceof ForestResponse)) {
            response.setResult(resultData);
        }
        this.resultData = (T) resultData;
        return resultData;
    }

    private void handleSaveCookie(ForestRequest request, ForestResponse response) {
        List<ForestCookie> cookieList = response.getCookies();
        if (cookieList != null && cookieList.size() > 0) {
            ForestCookies cookies = new ForestCookies(response.getCookies());
            handleSaveCookie(request, cookies);
        }
    }


    @Override
    public void handleSuccess(final Object resultData, ForestRequest request, ForestResponse response) {
        this.response = response;
        handleSaveCookie(request, response);
        request.getInterceptorChain().onSuccess(request, response);
        OnSuccess onSuccess = request.getOnSuccess();
        if (onSuccess != null) {
            onSuccess.onSuccess(request, response);
        }
    }

    @Override
    public void handleInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        request.getInterceptorChain().onInvokeMethod(request, method, args);
    }

    @Override
    public void handleError(ForestRequest request, ForestResponse response) {
        handleSaveCookie(request, response);
        ForestNetworkException networkException = new ForestNetworkException(
                "", response.getStatusCode(), response);
        handleError(request, response, networkException);
    }

    @Override
    public void handleError(ForestRequest request, ForestResponse response, Throwable ex) {
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
//            resultData = response.result();
//            return resultData;
        }
        else {
            throw e;
        }
    }

    @Override
    public byte[] handleBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
        return request.getInterceptorChain().onBodyEncode(request, encoder, encodedData);
    }

    @Override
    public void handleCanceled(ForestRequest request, ForestResponse response) {
        this.response = response;
        request.getInterceptorChain().onCanceled(request, response);
        if (request.getOnCanceled() != null) {
            request.getOnCanceled().onCanceled(request, response);
        }
    }

    @Override
    public void handleProgress(ForestRequest request, ForestProgress progress) {
        request.getInterceptorChain().onProgress(progress);
        OnProgress onProgress = request.getOnProgress();
        if (onProgress != null) {
            onProgress.onProgress(progress);
        }
    }

    @Override
    public void handleLoadCookie(ForestRequest request, ForestCookies cookies) {
        request.getInterceptorChain().onLoadCookie(request, cookies);
        OnLoadCookie onLoadCookie = request.getOnLoadCookie();
        if (onLoadCookie != null) {
            onLoadCookie.onLoadCookie(request, cookies);
        }
    }

    @Override
    public void handleSaveCookie(ForestRequest request, ForestCookies cookies) {
        request.getInterceptorChain().onSaveCookie(request, cookies);
        OnSaveCookie onSaveCookie = request.getOnSaveCookie();
        if (onSaveCookie != null) {
            onSaveCookie.onSaveCookie(request, cookies);
        }
    }

    @Override
    public Object handleResult(Object resultData) {
        this.resultData = (T) resultData;
        return resultData;
    }

    @Override
    public Object handleFuture(ForestRequest request, Future resultData) {
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

    public void setResultType(Type resultType) {
        this.resultType = ReflectUtils.toType(resultType);
        this.resultRawClass = ReflectUtils.toClass(resultType);
    }

    public void setResultRawClass(Class resultRawClass) {
        this.resultRawClass = resultRawClass;
    }

    public void setOnSuccessClassGenericType(Type onSuccessClassGenericType) {
        this.onSuccessClassGenericType = onSuccessClassGenericType;
    }

    @Override
    public Type getOnSuccessClassGenericType() {
        return onSuccessClassGenericType;
    }
}
