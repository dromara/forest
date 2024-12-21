package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;

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

    public ForestRequest getRequest() {
        return request;
    }

    public ForestResponse getResponse() {
        return response;
    }

    public String getRawData() {
        return rawData;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public SSEMessageResult getMessageResult() {
        return messageResult;
    }

    public ForestSSE sse() {
        return sse;
    }

    public void close() {
        sse.close();
        this.messageResult = SSEMessageResult.CLOSE;
    }
}
