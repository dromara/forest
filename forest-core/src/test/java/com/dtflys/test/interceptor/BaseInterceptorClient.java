package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Request;

@BaseRequest(
        interceptor = BaseInterceptor.class
)
public interface BaseInterceptorClient {

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plan"}
    )
    String none();

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plan"},
            interceptor = SimpleInterceptor.class
    )
    String simple();

}
