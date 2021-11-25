package com.dtflys.forest.backend;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;
import com.twitter.concurrent.Spool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Forest异步请求执行器
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.12
 */
public class AsyncHttpExecutor implements HttpExecutor {

    /**
     * Forest同步请求执行器
     */
    protected final HttpExecutor executor;

    /**
     * Forest响应对象处理器
     */
    protected final ResponseHandler responseHandler;

    /**
     * 异步线程计数
     */
    private final static AtomicInteger threadCount = new AtomicInteger(0);

    /**
     * 异步请求的执行线程池
     */
    protected final ThreadPoolExecutor pool;

    public AsyncHttpExecutor(HttpExecutor executor, ResponseHandler responseHandler) {
        this.executor = executor;
        this.responseHandler = responseHandler;
        pool = new ThreadPoolExecutor(
                8, Integer.MAX_VALUE,
                3, TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                tf -> {
                    Thread thread = new Thread(tf, "forest-async-" + threadCount.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                });
    }

    @Override
    public void execute(LifeCycleHandler lifeCycleHandler) {
        final CompletableFuture future = new CompletableFuture();
        pool.submit(() -> {
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
        responseHandler.handleFuture(future);
    }

    @Override
    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    @Override
    public void close() {

    }
}
