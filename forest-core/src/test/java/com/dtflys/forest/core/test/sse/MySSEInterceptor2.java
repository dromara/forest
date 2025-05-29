package com.dtflys.forest.core.test.sse;

import com.dtflys.forest.interceptor.SSEInterceptor;
import com.dtflys.forest.sse.EventSource;

public class MySSEInterceptor2 implements SSEInterceptor {

    @Override
    public void onMessage(EventSource event) {
        StringBuilder builder = (StringBuilder) event.request().getOrAddAttachment("text", StringBuilder::new);
        builder.append("id => " + event.id() + "\n");
        builder.append("event => " + event.event() + "\n");
        builder.append("data => " + event.data() + "\n");
    }
}
