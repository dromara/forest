package org.dromara.forest.core.test.interceptor;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.http.ForestResponse;

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
