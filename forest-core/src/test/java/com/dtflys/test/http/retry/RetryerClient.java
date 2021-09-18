package com.dtflys.test.http.retry;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Retryer;
import com.dtflys.forest.http.ForestRequest;

public interface RetryerClient {

    @Get("http://localhost:${port}/")
    @Retryer(TestRetryer.class)
    @Retry(maxRetryCount = "${0}")
    ForestRequest<String> testRetryRequest(int retryCount);


}
