package org.dromara.test.http.retry;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Retry;
import org.dromara.forest.callback.OnSuccess;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

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
    String testRetry(int retryCount, long retryInterval, OnSuccess<String> onSuccess);

    @Get("/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = ErrorRetryWhen.class)
    String testRetryWhenWithError(int retryCount, long retryInterval, OnSuccess<String> onSuccess);

}
