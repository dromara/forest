package com.dtflys.forest.backend;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.reflection.MethodLifeCycleHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Forest异步请求执行器
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.12
 */
public class AsyncHttpExecutor implements HttpExecutor {

    public final static Integer DEFAULT_MAX_THREAD_SIZE = 200;

    public final static Integer DEFAULT_MAX_QUEUE_SIZE = 100;

    protected final ForestConfiguration configuration;

    /**
     * Forest同步请求执行器
     */
    protected final HttpExecutor syncExecutor;

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
     * 初始化异步请求线程池
     *
     * @param maxAsyncThreadSize 最大异步线程数
     * @param maxQueueSize 最大线程池队列大小
     */
    public static synchronized void initAsyncThreads(Integer maxAsyncThreadSize, Integer maxQueueSize) {
        int threadSize = maxAsyncThreadSize != null ? maxAsyncThreadSize : DEFAULT_MAX_THREAD_SIZE;
        int queueSize = maxQueueSize == null ? DEFAULT_MAX_QUEUE_SIZE : maxQueueSize;
        BlockingQueue queue = queueSize > 0 ? new LinkedBlockingQueue<>(queueSize) : new SynchronousQueue<>();
        pool = new ThreadPoolExecutor(
                threadSize, threadSize,
                0, TimeUnit.MINUTES,
                queue,
                tf -> {
                    Thread thread = new Thread(tf, "forest-async-" + threadCount.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                }, new AsyncAbortPolicy());
    }

    /**
     * 获取最大异步线程数
     *
     * @return 最大异步线程数
     */
    public static int getMaxAsyncThreadSize() {
        return pool.getMaximumPoolSize();
    }


    /**
     * 获取异步线程池大小
     *
     * @return 异步线程池大小
     */
    public static int getAsyncThreadSize() {
        return pool.getPoolSize();
    }

    public AsyncHttpExecutor(ForestConfiguration configuration, HttpExecutor syncExecutor, ResponseHandler responseHandler) {
        this.configuration = configuration;
        this.syncExecutor = syncExecutor;
        this.responseHandler = responseHandler;
    }

    public static class AsyncTask implements Supplier {

        private final HttpExecutor executor;

        private final LifeCycleHandler lifeCycleHandler;

        public AsyncTask(HttpExecutor executor, LifeCycleHandler lifeCycleHandler) {
            this.executor = executor;
            this.lifeCycleHandler = lifeCycleHandler;
        }

        public LifeCycleHandler getLifeCycleHandler() {
            return lifeCycleHandler;
        }


        @Override
        public Object get() {
            executor.execute(lifeCycleHandler);
            if (lifeCycleHandler instanceof MethodLifeCycleHandler) {
                Object result = ((MethodLifeCycleHandler<?>) lifeCycleHandler).getResultData();
                return result;
            }
            return null;
        }
    }

    @Override
    public ForestRequest getRequest() {
        return syncExecutor.getRequest();
    }

    @Override
    public void execute(LifeCycleHandler lifeCycleHandler) {
        if (pool == null) {
            synchronized (this) {
                if (pool == null) {
                    initAsyncThreads(configuration.getMaxAsyncThreadSize(), configuration.getMaxAsyncQueueSize());
                }
            }
        }
        final AsyncTask task = new AsyncTask(syncExecutor, lifeCycleHandler);
        final CompletableFuture<?> future = CompletableFuture.supplyAsync(task, pool);
        responseHandler.handleFuture(getRequest(), future);
    }

    @Override
    public ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    @Override
    public ForestResponseFactory getResponseFactory() {
        return syncExecutor.getResponseFactory();
    }

    @Override
    public void close() {

    }

    /**
     * 关闭异步请求线程池
     *
     * @since 1.5.23
     */
    public static synchronized void closePool() {
        if (pool != null) {
            pool.shutdown();
        }
    }


    /**
     * 重启异步请求线程池
     *
     * @since 1.5.23
     */
    public static synchronized void restartPool() {
        if (pool != null) {
            pool.shutdown();
            pool = null;
        }
    }
}



