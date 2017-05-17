package org.forest.interceptor;

import org.forest.exceptions.ForestRuntimeException;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 17:01
 */
public class SimpleInterceptor implements Interceptor {
    @Override
    public boolean beforeExecute(ForestRequest request) {
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {

    }
}
