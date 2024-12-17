package com.dtflys.forest.sse;

/**
 * Forest SSE 消息消费者
 * @param <V> 消息中值的类型
 * @since 1.6.0
 */
@FunctionalInterface
public interface SSEMessageConsumer<V> {

    void onMessage(EventSource eventSource, String name, V value);

    default boolean matches(EventSource eventSource) {
        return true;
    }

}
