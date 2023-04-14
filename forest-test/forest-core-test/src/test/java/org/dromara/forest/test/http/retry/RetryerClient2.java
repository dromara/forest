package org.dromara.forest.test.http.retry;

import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Retry;
import org.dromara.forest.annotation.Retryer;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.retryer.BackOffRetryer;

@Retryer(TestRetryer.class)
@Retry(maxRetryCount = "${0}")
public interface RetryerClient2 {

    @Get("http://localhost:${port}/")
    ForestRequest<String> testRetryRequest(int retryCount);

    @Get("http://localhost:${port}/")
    @Retryer(BackOffRetryer.class)
    ForestRequest<String> testRetryRequest_backoff(int retryCount);

}
