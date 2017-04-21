package org.forest.interceptor;

import org.forest.reflection.ForestMethod;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.exceptions.ForestRuntimeException;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-06-26
 */
public interface ForestInterceptor {

    boolean beforeExecute(ForestMethod method, ForestRequest request);

    void onSuccess(Object data, ForestRequest request, ForestResponse response);

    void onError(ForestRuntimeException ex, ForestRequest request);

    void onCompleted(ForestMethod method, ForestRequest request, ForestResponse response);

}
