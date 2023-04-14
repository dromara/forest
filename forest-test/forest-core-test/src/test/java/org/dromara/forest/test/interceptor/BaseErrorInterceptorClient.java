package org.dromara.forest.test.interceptor;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestResponse;

@BaseRequest(interceptor = BaseErrorInterceptor.class)
public interface BaseErrorInterceptorClient {

    @Get(url = "http://this_is_a_error_address", timeout = 5)
    ForestResponse<String> testError();

    @Get(url = "http://this_is_a_error_address", timeout = 5)
    String testError2();
}
