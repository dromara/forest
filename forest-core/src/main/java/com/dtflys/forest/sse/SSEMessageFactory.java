package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;

public interface SSEMessageFactory {

    EventSource createEventSource(SSEEventList eventList, ForestSSE sse, ForestResponse response, String line);
}
