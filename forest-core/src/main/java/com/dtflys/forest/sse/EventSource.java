package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * Forest SSE Event Source
 *
 * @since v1.6.0
 */
public class EventSource {

    private final String name;

    private final ForestRequest request;

    private final ForestResponse response;

    private final String rawData;

    private final String value;

    private volatile SSEMessageResult messageResult = SSEMessageResult.PROCEED;

    public EventSource(String name, ForestRequest request, ForestResponse response) {
        this(name, request, response, null, null);
    }

    public EventSource(String name, ForestRequest request, ForestResponse response, String rawData, String value) {
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

    public void close() {
        messageResult = SSEMessageResult.CLOSE;
    }
}
