package com.dtflys.forest.backend;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncHttpExecutor implements HttpExecutor {

    protected final HttpExecutor executor;

    protected CompletableFuture future;

    protected final AtomicInteger threadCount = new AtomicInteger(0);

    protected ExecutorService executorService = Executors.newCachedThreadPool(tf -> {
        Thread thread = new Thread(tf, "forest-async-" + threadCount.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    });

    public AsyncHttpExecutor(HttpExecutor executor, ForestRequest request) {
        this.executor = executor;
    }


    @Override
    public void execute(LifeCycleHandler lifeCycleHandler) {
        executorService.submit(() -> {
            executor.execute(lifeCycleHandler);
        });
    }

    @Override
    public void close() {

    }
}
