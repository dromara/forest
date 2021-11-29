package com.dtflys.forest.backend;

import com.dtflys.forest.config.ForestConfiguration;
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

    protected final static Integer DEFAULT_MAX_THREAD_SIZE = 100;

    protected final ForestConfiguration configuration;

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
    protected static ThreadPoolExecutor pool;

    /**
     * 异步线程池是否已初始化
     */
    protected static volatile boolean initialized = false;

    /**
     * 初始化异步请求线程池
     *
     * @param configuration Forest配置对象
     */
    public static synchronized void initAsyncThreads(ForestConfiguration configuration) {
        if (initialized) {
            return;
        }
        Integer asyncThreadSize = configuration.getMaxAsyncThreadSize();
        pool = new ThreadPoolExecutor(
                10, asyncThreadSize != null ? asyncThreadSize : DEFAULT_MAX_THREAD_SIZE,
                3, TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                tf -> {
                    Thread thread = new Thread(tf, "forest-async-" + threadCount.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                });
        initialized = true;
    }

    /**
     *
     * @return
     */
    public static int getMaxAsyncThreadSize() {
        return pool.getMaximumPoolSize();
    }

    public static int getAsyncThreadSize() {
        return pool.getPoolSize();
    }

    public AsyncHttpExecutor(ForestConfiguration configuration, HttpExecutor executor, ResponseHandler responseHandler) {
        this.configuration = configuration;
        this.executor = executor;
        this.responseHandler = responseHandler;
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
