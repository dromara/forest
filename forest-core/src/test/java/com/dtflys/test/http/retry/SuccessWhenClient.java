package com.dtflys.test.http.retry;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.annotation.Success;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-16 22:20
 */
public interface SuccessWhenClient {

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "${0}")
    @Success(condition = TestSuccessWhen.class)
    ForestRequest<String> testRetryRequest(int retryCount, OnError onError);

}
