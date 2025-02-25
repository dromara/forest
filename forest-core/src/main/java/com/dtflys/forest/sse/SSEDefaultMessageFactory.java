package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.utils.StringUtils;

/**
 * 默认 SSE 消息工厂
 * 
 * @since 1.6.4
 */
public class SSEDefaultMessageFactory implements SSEMessageFactory {
    
    @Override
    public EventSource createEventSource(SSEEventList eventList, ForestSSE sse, ForestResponse response, String line) {
        final char firstChar = line.trim().charAt(0);
        if (firstChar == '[' || firstChar == '{' || firstChar == '<' || firstChar == '"' || firstChar == '\'') {
            return new EventSource(eventList, sse, "", sse.getRequest(), response, line, line);
        }
        final String[] group = line.split("\\:", 2);
        if (group.length == 1) {
            return new EventSource(eventList, sse, "", sse.getRequest(), response, line, line);
        }
        final String name = group[0].trim();
        final String data = StringUtils.trimBegin(group[1]);
        return new EventSource(eventList, sse, name, sse.getRequest(), response, line, data);
    }
    
}
