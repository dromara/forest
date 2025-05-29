package com.dtflys.forest.core.test.sse;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.core.test.model.Contact;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.SSEInterceptor;
import com.dtflys.forest.sse.EventSource;

import java.io.InputStream;

public class MySSEInterceptor implements SSEInterceptor {

    @Override
    public void onSuccess(InputStream data, ForestRequest request, ForestResponse response) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor onSuccess\n");
        System.out.println("");
   }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor afterExecute\n");
    }

    @Override
    public void onSSEOpen(EventSource eventSource) {
        StringBuilder builder = (StringBuilder) eventSource.request().getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor onSSEOpen\n");
    }

    @Override
    public void onSSEClose(EventSource eventSource) {
        StringBuilder builder = (StringBuilder) eventSource.request().getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor onSSEClose");
    }
    
    @SSEMessage("data")
    public void onMessage(EventSource eventSource) {
        System.out.println("MySSEInterceptor onMessage");
    }
    
    @SSEDataMessage
    public void onData(ForestRequest request, @SSEName String name, @SSEValue String value) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("Receive " + name + ": " + value + "\n");
    }

    @SSEEventMessage(valueRegex = "\\{.*name.*\\}")
    public void onEvent(ForestRequest request, @SSEValue Contact contact) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("name: ").append(contact.getName())
                .append("; age: ").append(contact.getAge())
                .append("; phone: ").append(contact.getPhone())
                .append("\n");
    }

    @SSEEventMessage(valuePrefix = "close")
    public void onEventClose(EventSource eventSource, ForestRequest request, @SSEValue String value) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("receive close --- " + value).append("\n");
        eventSource.close();
    }

}
