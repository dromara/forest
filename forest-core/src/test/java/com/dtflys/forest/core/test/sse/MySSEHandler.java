package com.dtflys.forest.core.test.sse;

import com.dtflys.forest.annotation.SSEEventMessage;
import com.dtflys.forest.annotation.SSEMessage;
import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.core.test.model.Contact;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.sse.EventSource;


public class MySSEHandler extends ForestSSE {

    private final StringBuffer buffer = new StringBuffer();

    @Override
    protected void onOpen(EventSource event) {
        buffer.append("SSE Open").append("\n");
    }

    @Override
    protected void onClose(EventSource event) {
        buffer.append("SSE Close");
    }

    @Override
    public void onMessage(EventSource event, String name, String value) {
    }

    @SSEMessage("data")
    public void onData(EventSource event, @SSEName String name, @SSEValue String value) {
        buffer.append("Receive ").append(name).append(": ").append(value).append("\n");
    }

    @SSEEventMessage(valueRegex = "\\{.*name.*\\}")
    public void onEvent(@SSEValue Contact contact) {
        buffer.append("name: ").append(contact.getName())
                .append("; age: ").append(contact.getAge())
                .append("; phone: ").append(contact.getPhone())
                .append("\n");
    }

    @SSEEventMessage(valuePrefix = "close")
    public void onEventClose(EventSource eventSource, @SSEValue String value) {
        buffer.append("receive close --- " + value).append("\n");
        eventSource.close();
    }

    public StringBuffer getStringBuffer() {
        return buffer;
    }
}
