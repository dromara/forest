package org.dromara.forest.test.http.retry;

import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Retry;
import org.dromara.forest.callback.OnSuccess;

@Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = TestRetryWhen.class)
public interface RetryClient2 {

    @Get("http://localhost:${port}/")
    String testRetry(int retryCount, long retryInterval, OnSuccess<String> onSuccess);

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "${0}", maxRetryInterval = "${1}", condition = TestRetryWhen2.class)
    String testRetry_not_retry(int retryCount, long retryInterval, OnSuccess<String> onSuccess);


}
