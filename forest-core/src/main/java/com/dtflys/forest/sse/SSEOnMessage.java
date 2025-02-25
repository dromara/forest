package com.dtflys.forest.sse;

@FunctionalInterface
public interface SSEOnMessage {

    void onMessage(EventSource event);

}
