package com.dtflys.forest.sse;

@FunctionalInterface
public interface SSEMessageConsumer {

    void onMessage(EventSource eventSource, String name, String value);

    default boolean matches(EventSource eventSource) {
        return true;
    }

    default SSEMessageResult proceed() {
        return SSEMessageResult.PROCEED;
    }

    default SSEMessageResult close() {
        return SSEMessageResult.CLOSE;
    }

}
