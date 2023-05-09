package org.dromara.forest.core.test.http.retry;

import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Retry;
import org.dromara.forest.annotation.Success;
import org.dromara.forest.callback.OnError;
import org.dromara.forest.http.ForestRequest;

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
