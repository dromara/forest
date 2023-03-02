package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;

@BaseRequest(
        interceptor = BaseInterceptor.class
)
public interface BaseInterceptorClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String none();

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "POST",
            interceptor = SimpleInterceptor.class
    )
    String simple(@Body String body);

    @Request(
            url = "http://localhost:${port}/hello/user",
            interceptor = SimpleInterceptor.class
    )
    ForestResponse generationType();

}
