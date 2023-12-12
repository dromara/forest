package com.dtflys.test.http.retry;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

@BaseRequest(baseURL = "http://localhost:${port}/", interceptor = TestRetryInterceptor.class)
public interface RetryClient {

    @Get("/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = TestRetryWhen.class)
    ForestRequest<String> testRetryRequest(int retryCount, long retryInterval);

    @Get("/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = TestRetryWhen404.class)
    ForestRequest<ForestResponse> testRetryRequest_404(int retryCount, long retryInterval);

    @Get("/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = TestRetryWhen.class)
    String testRetry(int retryCount, long retryInterval, OnSuccess onSuccess);

    @Get("/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = ErrorRetryWhen.class)
    String testRetryWhenWithError(int retryCount, long retryInterval, OnSuccess onSuccess);

}
