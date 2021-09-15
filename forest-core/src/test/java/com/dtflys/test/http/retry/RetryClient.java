package com.dtflys.test.http.retry;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestRequest;

public interface RetryClient {

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = TestRetryWhen.class)
    ForestRequest<String> testRetryRequest(int retryCount, long retryInterval);

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = TestRetryWhen.class)
    String testRetry(int retryCount, long retryInterval, OnSuccess<String> onSuccess);

}
