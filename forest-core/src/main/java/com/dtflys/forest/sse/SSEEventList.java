package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.utils.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class SSEEventList {
    
    public final static int MAX_EVENTS_CAPACITY = 32;

    private final ForestSSE sse;

    private final ForestRequest request;

    private final ForestResponse response;

    private final List<EventSource> eventSources = new ArrayList<>(MAX_EVENTS_CAPACITY);

    public ForestSSE sse() {
        return sse;
    }

    public ForestRequest request() {
        return request;
    }

    public ForestResponse response() {
        return response;
    }

    public SSEEventList(ForestSSE sse, ForestRequest request, ForestResponse response) {
        this.sse = sse;
        this.request = request;
        this.response = response;
    }

    void addEventSource(EventSource eventSource) {
        eventSources.add(eventSource);
    }
    
    public EventSource eventSource(String name) {
        for (EventSource eventSource : eventSources) {
            if (eventSource.name().equals(name)) {
                return eventSource;
            }
        }
        return null;
    }
    
    public int size() {
        return eventSources.size();
    }
    
    public boolean isEmpty() {
        return eventSources.isEmpty();
    }
    
    public EventSource get(int index) {
        return eventSources.get(index);
    }
    
    public String name(int index) {
        return eventSources.get(index).name();
    }
    
    public String lastName() {
        return eventSources.get(eventSources.size() - 1).name();
    }
    
    public String lastValue() {
        return eventSources.get(eventSources.size() - 1).value();
    }
    
    public String value(int index) {
        return eventSources.get(index).value();
    }

    public <T> T value(int index, Class<T> clazz) {
        final EventSource eventSource = get(index);
        if (eventSource == null) {
            return null;
        }
        return eventSource.value(clazz);
    }

    public <T> T value(int index, TypeReference<T> typeReference) {
        final EventSource eventSource = get(index);
        if (eventSource == null) {
            return null;
        }
        return eventSource.value(typeReference);
    }


    public String value(String name) {
        final EventSource eventSource = eventSource(name);
        if (eventSource == null) {
            return null;
        }
        return eventSource.value();
    }
    
    public <T> T value(String name, Class<T> clazz) {
        final EventSource eventSource = eventSource(name);
        if (eventSource == null) {
            return null;
        }
        return eventSource.value(clazz);
    }
    
    public <T> T value(String name, TypeReference<T> typeReference) {
        final EventSource eventSource = eventSource(name);
        if (eventSource == null) {
            return null;
        }
        return eventSource.value(typeReference);
    }

    public String data() {
        return value("data");
    }

    public <T> T data(Class<T> clazz) {
        return value("data", clazz);
    }

    public <T> T data(TypeReference<T> typeReference) {
        return value("data", typeReference);
    }

    public String event() {
        return value("event");
    }
    
    public <T> T event(Class<T> clazz) {
        return value("event", clazz);
    }
    
    public <T> T event(TypeReference<T> typeReference) {
        return value("event", typeReference);
    }
    
    public String id() {
        return value("id");
    }
    
    public <T> T id(Class<T> clazz) {
        return value("id", clazz);
    }
    
    public <T> T id(TypeReference<T> typeReference) {
        return value("id", typeReference);
    }
    
    public String retry() {
        return value("retry");
    }
    
    public <T> T retry(Class<T> clazz) {
        return value("retry", clazz);
    }
    
    public <T> T retry(TypeReference<T> typeReference) {
        return value("retry", typeReference);
    }

    public SSEEventList close() {
        sse.close();
        return this;
    }


}
