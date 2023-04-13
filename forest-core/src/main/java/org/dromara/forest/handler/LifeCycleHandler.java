package org.dromara.forest.handler;

import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.http.ForestCookies;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.utils.ForestProgress;

import java.lang.reflect.Type;
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

    Object handleResultType(ForestRequest request, ForestResponse response, Type resultType, Class resultClass);

    void handleSuccess(Object resultData, ForestRequest request, ForestResponse response);

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
