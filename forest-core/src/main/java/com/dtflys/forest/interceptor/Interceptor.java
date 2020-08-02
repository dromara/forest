package com.dtflys.forest.interceptor;

import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.ForestProgress;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-06-26
 */
public interface Interceptor<T> extends OnSuccess<T>, OnError, OnProgress {

    boolean beforeExecute(ForestRequest request);

    void afterExecute(ForestRequest request, ForestResponse response);

    @Override
    default void onProgress(ForestProgress progress) {
    }
}
