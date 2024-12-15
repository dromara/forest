package com.dtflys.forest.sse;

@FunctionalInterface
public interface SSEMessageConsumer<V> {

    void onMessage(EventSource eventSource, String name, V value);

    default boolean matches(EventSource eventSource) {
        return true;
    }

}
