package org.forest.callback;

import org.forest.http.ForestResponse;
import org.forest.http.ForestRequest;

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
