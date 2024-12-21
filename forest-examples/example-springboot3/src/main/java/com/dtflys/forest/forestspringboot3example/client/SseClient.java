package com.dtflys.forest.forestspringboot3example.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.backend.httpclient.HttpClient;
import com.dtflys.forest.forestspringboot3example.sse.MySSEHandler;
import com.dtflys.forest.http.ForestSSE;

@HttpClient
@BaseRequest(baseURL = "localhost:#{server.port}")
public interface SseClient {
    
    
    @Get(url = "/sse", contentType = "text/event-stream")
    ForestSSE stream();

    @Get(url = "/sse", contentType = "text/event-stream")
    MySSEHandler streamWithMyHandler();
}
