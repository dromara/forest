package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.TypeReference;

/**
 * Forest SSE 事件来源
 *
 * @since 1.6.0
 */
public class EventSource {
    
    private final ForestSSE sse;
    
    private final SSEEventList eventList;

    private final String name;

    private final ForestRequest request;

    private final ForestResponse response;

    private final String rawData;

    private final String value;

    private volatile SSEMessageResult messageResult = SSEMessageResult.PROCEED;

    public EventSource(SSEEventList eventList, ForestSSE sse, String name, ForestRequest request, ForestResponse response) {
        this(eventList, sse, name, request, response, null, null);
    }

    public EventSource(SSEEventList eventList, ForestSSE sse, String name, ForestRequest request, ForestResponse response, String rawData, String value) {
        this.eventList = eventList == null ? new SSEEventList(sse, request, response) : eventList;
        this.sse = sse;
        this.name = name;
        this.request = request;
        this.response = response;
        this.rawData = rawData;
        this.value = value;
        this.eventList.addEventSource(this);
    }

    public ForestRequest request() {
        return request;
    }

    public ForestResponse response() {
        return response;
    }

    public String rawData() {
        return rawData;
    }
    
    public String name() {
        return name;
    }
    
    public String name(int index) {
        return eventList.name(index);
    }
    
    public SSEEventList list() {
        return eventList;
    }
    
    public EventSource get(int index) {
        return eventList.get(index);
    }

    public String value() {
        return value;
    }
    
    public String value(int index) {
        return eventList.value(index);
    }
    
    public <T> T value(int index, Class<T> type) {
        return eventList.value(index, type);
    }
    
    public <T> T value(int index, TypeReference<T> type) {
        return eventList.value(index, type);
    }
    
    public <T> T value(Class<T> type) {
        T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(value, type);
        return encodedValue;
    }

    public <T> T value(TypeReference<T> typeReference) {
        T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(value, typeReference);
        return encodedValue;
    }
    
    public String value(String name) {
        return this.eventList.value(name);
    }
    
    public <T> T value(String name, Class<T> type) {
        return this.eventList.value(name, type);
    }
    
    public <T> T value(String name, TypeReference<T> typeReference) {
        return this.eventList.value(name, typeReference);
    }
    
    public String data() {
        return value("data");
    }
    
    public <T> T data(Class<T> type) {
        return value("data", type);
    }
    
    public <T> T data(TypeReference<T> typeReference) {
        return value("data", typeReference);
    }
    
    public String event() {
        return value("event");
    }
    
    public <T> T event(Class<T> type) {
        return value("event", type);
    }
    
    public <T> T event(TypeReference<T> typeReference) {
        return value("event", typeReference);
    }
    
    public String id() {
        return value("id");
    }
    
    public <T> T id(Class<T> type) {
        return value("id", type);
    }
    
    public <T> T id(TypeReference<T> typeReference) {
        return value("id", typeReference);
    }

    public SSEMessageResult messageResult() {
        return messageResult;
    }

    public ForestSSE sse() {
        return sse;
    }

    public EventSource close() {
        sse.close();
        this.messageResult = SSEMessageResult.CLOSE;
        return this;
    }
    
}
