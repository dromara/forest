package com.dtflys.forest.example.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.backend.httpclient.HttpClient;
import com.dtflys.forest.backend.okhttp3.OkHttp3;
import com.dtflys.forest.example.sse.MySSEHandler;
import com.dtflys.forest.http.ForestSSE;

@HttpClient
@BaseRequest(baseURL = "localhost:#{server.port}")
public interface SseClient {
    
    
    @Get(url = "/sse", contentType = "text/event-stream")
    ForestSSE stream();

    @Get(url = "/sse", contentType = "text/event-stream")
    MySSEHandler streamWithMyHandler();
}
