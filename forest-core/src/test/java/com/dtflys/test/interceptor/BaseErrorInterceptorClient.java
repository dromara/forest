package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;

@BaseRequest(interceptor = BaseErrorInterceptor.class)
public interface BaseErrorInterceptorClient {

    @Get(url = "http://this_is_a_error_address", timeout = 5)
    ForestResponse<String> testError();

}
