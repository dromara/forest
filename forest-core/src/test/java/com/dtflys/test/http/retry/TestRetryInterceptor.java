package com.dtflys.test.http.retry;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;

public class TestRetryInterceptor implements Interceptor<Object> {

    @Override
    public void onRetry(ForestRequest request, ForestResponse response) {
        request.addAttachment("retry-interceptor", request.getCurrentRetryCount());
    }
}
