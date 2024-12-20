package com.dtflys.forest.solon.test.sse;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestSSE;

@ForestClient
@BaseRequest(baseURL = "http://localhost:{port}")
public interface MySSEClient {
    
    @Get("/sse")
    ForestSSE testSSE();

    @Get(url = "/sse", interceptor = MySSEInterceptor.class)
    ForestSSE testSSE_withInterceptor();
}
