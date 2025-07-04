package com.dtflys.forest.forestspringboot3example.sse;

import com.dtflys.forest.annotation.SSEDataMessage;
import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.sse.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySSEHandler extends ForestSSE {
    
    private final static Logger log = LoggerFactory.getLogger(MySSEHandler.class);
    
    @SSEDataMessage
    public void onData(@SSEName String name, @SSEValue String value) {
        log.info("MySSEHandler onData: name = {}, value = {}", name, value);
    }


    @Override
    protected void onClose(EventSource eventSource) {
        log.info("SSE Closed");
    }
}
