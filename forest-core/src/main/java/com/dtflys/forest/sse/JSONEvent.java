package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;

public class JSONEvent extends EventSource {
    
    public JSONEvent(SSEEventList eventGroup, ForestSSE sse, String name, ForestRequest request, ForestResponse response) {
        super(eventGroup, sse, name, request, response);
    }
}
