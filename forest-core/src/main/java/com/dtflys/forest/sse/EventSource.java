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

    private final String name;

    private final ForestRequest request;

    private final ForestResponse response;

    private final String rawData;

    private final String value;

    private volatile SSEMessageResult messageResult = SSEMessageResult.PROCEED;

    public EventSource(ForestSSE sse, String name, ForestRequest request, ForestResponse response) {
        this(sse, name, request, response, null, null);
    }

    public EventSource(ForestSSE sse, String name, ForestRequest request, ForestResponse response, String rawData, String value) {
        this.sse = sse;
        this.name = name;
        this.request = request;
        this.response = response;
        this.rawData = rawData;
        this.value = value;
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

    public String value() {
        return value;
    }
    
    public <T> T value(Class<T> type) {
        T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(value, type);
        return encodedValue;
    }

    public <T> T value(TypeReference<T> typeReference) {
        T encodedValue = (T) request.getConfiguration().getConverter(ForestDataType.AUTO).convertToJavaObject(value, typeReference);
        return encodedValue;
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
