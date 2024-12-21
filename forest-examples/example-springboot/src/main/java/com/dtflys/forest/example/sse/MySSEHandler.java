package com.dtflys.forest.example.sse;

import com.dtflys.forest.annotation.SSEDataMessage;
import com.dtflys.forest.annotation.SSEEventMessage;
import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.sse.EventSource;
import io.swagger.annotations.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySSEHandler extends ForestSSE {
    
    private final static Logger log = LoggerFactory.getLogger(MySSEHandler.class);
    
   
    @SSEDataMessage
    public void onData(EventSource eventSource, @SSEName String name, @SSEValue String value) {
        log.info("MySSEHandler onData: name = {}, value = {}", name, value);
        if ("close".equals(value)) {
            eventSource.close();
        }
    }

    @Override
    protected void onOpen(EventSource eventSource) {
        log.info("MySSEHandler: onOpen");
    }

    @Override
    public void onClose(ForestRequest request, ForestResponse response) {
        log.info("MySSEHandler: onClose");
    }

    @SSEEventMessage(valueRegex = "\\{.*name.*\\}")
    public void onEvent(@SSEValue Contact contact) {
        // 监听名称为 event 的消息事件
        // 并且消息要满足匹配正则表达式 "\\{.*name.*\\}" 的要求
        log.info("MySSEHandler: onEvent");
        
    }

}
