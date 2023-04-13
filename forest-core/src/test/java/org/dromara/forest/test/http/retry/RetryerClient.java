package org.dromara.forest.test.http.retry;

import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Retry;
import org.dromara.forest.annotation.Retryer;
import org.dromara.forest.http.ForestRequest;

public interface RetryerClient {

    @Get("http://localhost:${port}/")
    @Retryer(TestRetryer.class)
    @Retry(maxRetryCount = "${0}")
    ForestRequest<String> testRetryRequest(int retryCount);


}
