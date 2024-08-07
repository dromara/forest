package com.dtflys.forest.handler;

import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ResponseResult;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.retryer.ForestRetryer;
import com.dtflys.forest.utils.ForestProgress;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.Future;


/**
 * Forest生命周期处理器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 16:49
 */
public interface LifeCycleHandler {

    Object handleSync(ForestRequest request, ForestResponse response);

    Object handleSyncWithException(ForestRequest request, ForestResponse response, Throwable ex);

    Object handleResultType(ForestRequest request, ForestResponse response);

    Object handleResultType(Optional<?> resultOpt, ForestRequest request, ForestResponse response, Type resultType, Class resultClass);

    Object handleResultType(ForestRequest request, ForestResponse response, Type resultType, Class resultClass);

    ResponseResult handleResponse(ForestRequest request, ForestResponse response);

    void handleSuccess(Object resultData, final Optional<?> resultOpt, ForestRequest request, ForestResponse response);

    void handleInvokeMethod(ForestRequest request, ForestMethod method, Object[] args);

    Object handleError(ForestRequest request, ForestResponse response);

    Object handleError(ForestRequest request, ForestResponse response, Throwable ex);

    byte[] handleBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData);

    void handleCanceled(ForestRequest request, ForestResponse response);

    void handleProgress(ForestRequest request, ForestProgress progress);

    void handleLoadCookie(ForestRequest request, ForestCookies cookies);

    void handleSaveCookie(ForestRequest request, ForestCookies cookies);

    Object handleResult(Object resultData);

    Object handleFuture(ForestRequest request, Future resultData);

    Type getOnSuccessClassGenericType();

    Type getResultType();

}
