package com.dtflys.forest.test.http.sse;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.test.sse.MySSEHandler;

@Address(host = "localhost", port = "{port}")
public interface SSEClient {

    @Get("/sse")
    ForestSSE testSSE();

    @Get("/sse")
    MySSEHandler testSSE_withCustomClass();

}
