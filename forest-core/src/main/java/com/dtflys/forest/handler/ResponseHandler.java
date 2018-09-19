package com.dtflys.forest.handler;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

import java.lang.reflect.Type;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 16:49
 */
public interface ResponseHandler {

    Object handleSync(ForestRequest request, ForestResponse response);

    Object handleSyncWitchException(ForestRequest request, ForestResponse response, Exception ex);

    Object handleResultType(ForestRequest request, ForestResponse response);

    Object handleResultType(ForestRequest request, ForestResponse response, Type resultType, Class resultClass);

    Object handleSuccess(Object resultData, ForestRequest request, ForestResponse response);

    void handleError(ForestRequest request, ForestResponse response);

    void handleError(ForestRequest request, ForestResponse response, Exception ex);

    Object handleResult(Object resultData);

    Class getOnSuccessClassGenericType();

    Type getReturnType();

}
