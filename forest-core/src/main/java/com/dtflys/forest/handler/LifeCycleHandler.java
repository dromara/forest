package com.dtflys.forest.handler;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.ForestProgress;

import java.lang.reflect.Type;


/**
 * Forest生命周期处理器
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 16:49
 */
public interface LifeCycleHandler {

    Object handleSync(ForestRequest request, ForestResponse response);

    Object handleSyncWitchException(ForestRequest request, ForestResponse response, Exception ex);

    Object handleResultType(ForestRequest request, ForestResponse response);

    Object handleResultType(ForestRequest request, ForestResponse response, Type resultType, Class resultClass);

    Object handleSuccess(Object resultData, ForestRequest request, ForestResponse response);

    void handleInvokeMethod(ForestRequest request, ForestMethod method, Object[] args);

    void handleError(ForestRequest request, ForestResponse response);

    void handleError(ForestRequest request, ForestResponse response, Exception ex);

    void handleProgress(ForestRequest request, ForestProgress progress);

    Object handleResult(Object resultData);

    Type getOnSuccessClassGenericType();

    Type getReturnType();

}
