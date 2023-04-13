package org.dromara.forest.test.interceptor;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;

public class PostHeadInterceptor implements Interceptor {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        request.addHeader("accessToken", "11111111");
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
