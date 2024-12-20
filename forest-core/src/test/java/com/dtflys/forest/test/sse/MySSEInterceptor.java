package com.dtflys.forest.test.sse;

import com.dtflys.forest.annotation.SSEDataMessage;
import com.dtflys.forest.annotation.SSEEventMessage;
import com.dtflys.forest.annotation.SSEName;
import com.dtflys.forest.annotation.SSEValue;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.SSEInterceptor;
import com.dtflys.forest.sse.EventSource;
import com.dtflys.forest.test.model.Contact;

public class MySSEInterceptor implements SSEInterceptor {


    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor onSuccess\n");
   }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor afterExecute\n");
    }

    @Override
    public void onSSEOpen(EventSource eventSource) {
        StringBuilder builder = (StringBuilder) eventSource.getRequest().getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor onSSEOpen\n");
    }

    @Override
    public void onSSEClose(ForestRequest request, ForestResponse response) {
        StringBuilder builder = (StringBuilder) request.getOrAddAttachment("text", StringBuilder::new);
        builder.append("MySSEInterceptor onSSEClose");
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
