package com.dtflys.forest.reflection;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ForestProgress;

import java.lang.reflect.Type;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-08 17:42
 */
public class NoneLifeCycleHandler implements LifeCycleHandler {
    @Override
    public Object handleSync(ForestRequest request, ForestResponse response) {
        return null;
    }

    @Override
    public Object handleSyncWitchException(ForestRequest request, ForestResponse response, Exception ex) {
        return null;
    }

    @Override
    public Object handleResultType(ForestRequest request, ForestResponse response) {
        return null;
    }

    @Override
    public Object handleResultType(ForestRequest request, ForestResponse response, Type resultType, Class resultClass) {
        return null;
    }

    @Override
    public Object handleSuccess(Object resultData, ForestRequest request, ForestResponse response) {
        return null;
    }

    @Override
    public void handleInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {

    }

    @Override
    public void handleError(ForestRequest request, ForestResponse response) {

    }

    @Override
    public void handleError(ForestRequest request, ForestResponse response, Exception ex) {

    }

    @Override
    public void handleProgress(ForestRequest request, ForestProgress progress) {

    }

    @Override
    public Object handleResult(Object resultData) {
        return null;
    }

    @Override
    public Type getOnSuccessClassGenericType() {
        return null;
    }

    @Override
    public Type getReturnType() {
        return null;
    }
}
