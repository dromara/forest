package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface ForestSSEListener<T extends ForestSSEListener<T>> extends SSEStringMessageConsumer {


    ForestRequest getRequest();

    <R extends T> R listen();

    <R extends T> CompletableFuture<R> asyncListen();

    <R extends T> CompletableFuture<R> asyncListen(ExecutorService pool);

}
