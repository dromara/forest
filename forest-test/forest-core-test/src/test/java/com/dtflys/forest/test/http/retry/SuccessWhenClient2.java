package com.dtflys.forest.test.http.retry;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Success;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-16 22:20
 */
@Success(condition = TestSuccessWhen.class)
public interface SuccessWhenClient2 {

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "3")
    ForestRequest<String> testRetryRequest(int retryCount, OnError onError);

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "3")
    @Success(condition = TestSuccessWhen2.class)
    ForestRequest<String> testRetryRequest_success(int retryCount, OnError onError);


}
