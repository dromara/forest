package com.dtflys.forest.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-06-26
 */
public interface Interceptor<T> {

    boolean beforeExecute(ForestRequest request);

    void onSuccess(T data, ForestRequest request, ForestResponse response);

    void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response);

    void afterExecute(ForestRequest request, ForestResponse response);

}
