package com.dtflys.forest.sse;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface ForestSSEListener extends SSEMessageConsumer {


    ForestRequest getRequest();

    ForestResponse listen();

    CompletableFuture<ForestResponse> asyncListen();

    CompletableFuture<ForestResponse> asyncListen(ExecutorService pool);

}
