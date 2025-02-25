package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestSSE;

/**
 * SSE 消息工厂
 * 
 * @since 1.6.4
 */
public interface SSEMessageFactory {

    /**
     * 创建 SSE 事件
     * 
     * @param eventList 事件列表
     * @param sse SSE 对象
     * @param response 响应对象
     * @param line 行字符串
     * @return SSE 事件
     */
    EventSource createEventSource(SSEEventList eventList, ForestSSE sse, ForestResponse response, String line);
}
