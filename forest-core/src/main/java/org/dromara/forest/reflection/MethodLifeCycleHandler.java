package org.dromara.forest.reflection;

import org.dromara.forest.callback.OnLoadCookie;
import org.dromara.forest.callback.OnProgress;
import org.dromara.forest.callback.OnSaveCookie;
import org.dromara.forest.callback.OnSuccess;
import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.exceptions.ForestNetworkException;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.handler.ResultHandler;
import org.dromara.forest.http.ForestCookie;
import org.dromara.forest.http.ForestCookies;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.utils.ForestProgress;
import org.dromara.forest.utils.ReflectUtil;

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
        this.resultType = ReflectUtil.toType(resultType);
        this.resultRawClass = ReflectUtil.toClass(resultType);
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
                    resultData = response.getResult();
                }
            } else {
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
        request.getInterceptorChain().onSuccess(resultData, request, response);
        OnSuccess onSuccess = request.getOnSuccess();
        if (onSuccess != null) {
            Object result = RESULT_HANDLER.getResult(request, response, onSuccessClassGenericType, ReflectUtil.toClass(onSuccessClassGenericType));
            onSuccess.onSuccess(result, request, response);
        }
    }

    @Override
    public void handleInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        request.getInterceptorChain().onInvokeMethod(request, method, args);
    }

    @Override
    public Object handleError(ForestRequest request, ForestResponse response) {
        handleSaveCookie(request, response);
        ForestNetworkException networkException = new ForestNetworkException(
                "", response.getStatusCode(), response);
        return handleError(request, response, networkException);
    }

    @Override
    public Object handleError(ForestRequest request, ForestResponse response, Throwable ex) {
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
        this.resultType = ReflectUtil.toType(resultType);
        this.resultRawClass = ReflectUtil.toClass(resultType);
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
