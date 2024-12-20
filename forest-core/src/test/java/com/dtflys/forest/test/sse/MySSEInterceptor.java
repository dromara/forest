package com.dtflys.forest.test.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySSEInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MySSEInterceptor.class);

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("MySSEInterceptor onSuccess");
    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("MySSEInterceptor afterExecute");
    }
}
