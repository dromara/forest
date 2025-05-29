package com.dtflys.forest.core.test.sse;

import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.sse.EventSource;

public class MySSEHandler2 extends ForestSSE {
    
    StringBuffer stringBuffer = new StringBuffer();

    @Override
    public void onMessage(EventSource event) {
        stringBuffer.append("id => " + event.id() + "\n");
        stringBuffer.append("event => " + event.event() + "\n");
        stringBuffer.append("data => " + event.data() + "\n");
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }
}
