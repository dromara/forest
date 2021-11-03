package com.dtflys.forest.backend;

import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncHttpExecutor implements HttpExecutor {

    protected final HttpExecutor executor;

    protected final ResponseHandler responseHandler;

    protected final AtomicInteger threadCount = new AtomicInteger(0);

    protected ExecutorService executorService = Executors.newCachedThreadPool(tf -> {
        Thread thread = new Thread(tf, "forest-async-" + threadCount.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    });

    public AsyncHttpExecutor(HttpExecutor executor, ForestRequest request, ResponseHandler responseHandler) {
        this.executor = executor;
        this.responseHandler = responseHandler;
    }


    @Override
    public void execute(LifeCycleHandler lifeCycleHandler) {
        final CompletableFuture future = new CompletableFuture();
        final Date startTime = new Date();
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        executorService.submit(() -> {
            try {
                executor.execute(lifeCycleHandler);
                if (lifeCycleHandler instanceof MethodLifeCycleHandler) {
                    Object result = ((MethodLifeCycleHandler<?>) lifeCycleHandler).getResultData();
                    future.complete(result);
                } else {
                    future.complete(null);
                }
            } catch (Throwable th) {
                future.completeExceptionally(th);
            }
        });
        responseHandler.handleFuture(future, startTime, forestResponseFactory);
    }

    @Override
    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    @Override
    public void close() {

    }
}
