package org.forest.interceptor;

import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.exceptions.ForestRuntimeException;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-06-26
 */
public interface Interceptor {

    boolean beforeExecute(ForestRequest request);

    void onSuccess(Object data, ForestRequest request, ForestResponse response);

    void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response);

    void afterExecute(ForestRequest request, ForestResponse response);

}
