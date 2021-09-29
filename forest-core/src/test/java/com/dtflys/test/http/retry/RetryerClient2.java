package com.dtflys.test.http.retry;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Retryer;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.forest.retryer.NoneRetryer;

@Retryer(TestRetryer.class)
@Retry(maxRetryCount = "${0}")
public interface RetryerClient2 {

    @Get("http://localhost:${port}/")
    ForestRequest<String> testRetryRequest(int retryCount);

    @Get("http://localhost:${port}/")
    @Retryer(BackOffRetryer.class)
    ForestRequest<String> testRetryRequest_backoff(int retryCount);

}
