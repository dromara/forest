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
public interface SuccessWhenClient {

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "${0}")
    @Success(condition = TestSuccessWhen.class)
    ForestRequest<String> testRetryRequest_with_successWhen(int retryCount, OnError onError);

    @Get("http://localhost:${port}/")
    @Retry(maxRetryCount = "${0}")
    @Success(condition = ErrorSuccessWhen.class)
    ForestRequest<String> testRetryRequest_with_error_successWhen(int retryCount, OnError onError);

}
