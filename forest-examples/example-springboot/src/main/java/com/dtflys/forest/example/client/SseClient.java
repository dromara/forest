package com.dtflys.forest.example.client;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.backend.okhttp3.OkHttp3;
import com.dtflys.forest.example.sse.MySSEHandler;
import com.dtflys.forest.http.ForestSSE;

@OkHttp3
@Address(host = "localhost", port = "#{server.port}")
public interface SseClient {
    
    
    @Get(url = "/sse", contentType = "text/event-stream")
    ForestSSE stream();

    @Get(url = "/sse", contentType = "text/event-stream")
    MySSEHandler streamWithMyHandler();
}
