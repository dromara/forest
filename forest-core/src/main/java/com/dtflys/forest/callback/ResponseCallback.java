package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * Request Callback Interface
 * @author gongjun
 * @since 2016-05-03
 */
public interface ResponseCallback<T> {

    /**
     * callback method on success
     * @param data
     * @param requst
     * @param response
     */
    void onSuccess(T data, ForestRequest requst, ForestResponse response);

    /**
     * calback method on error
     * @param errorCode
     * @param requst
     * @param response
     */
    void onError(Integer errorCode, ForestRequest requst, ForestResponse response);
}
