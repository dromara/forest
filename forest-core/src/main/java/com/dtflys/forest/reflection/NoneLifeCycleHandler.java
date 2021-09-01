package com.dtflys.forest.reflection;

import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.retryer.Retryer;
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
    public Object handleSyncWithException(ForestRequest request, ForestResponse response, Exception ex) {
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
    public Object handleError(ForestRequest request, ForestResponse response) {
        return null;
    }

    @Override
    public Object handleError(ForestRequest request, ForestResponse response, Throwable ex) {
        return null;
    }

    @Override
    public void handleTry(ForestRetryException ex, Retryer retryer) throws Throwable {

    }

    @Override
    public void handleProgress(ForestRequest request, ForestProgress progress) {

    }

    @Override
    public void handleLoadCookie(ForestRequest request, ForestCookies cookies) {

    }

    @Override
    public void handleSaveCookie(ForestRequest request, ForestCookies cookies) {

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
    public Type getResultType() {
        return null;
    }
}
