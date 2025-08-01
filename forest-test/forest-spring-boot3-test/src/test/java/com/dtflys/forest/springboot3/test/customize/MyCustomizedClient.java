package com.dtflys.forest.springboot3.test.customize;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;

@BaseRequest(baseURL = "http://localhost:${port}")
public interface MyCustomizedClient {

    @Post("/test/headers")
    @MyHeaders
    String testHeaders();

}
