package com.dtflys.forest.config;

import com.dtflys.forest.backend.AsyncAbortPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Forest异步请求线程池管理类
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.29
 */
public class AsyncThreadPools {

    /**
     * 默认异步请求线程池最大线程数
     */
    public final static Integer DEFAULT_MAX_THREAD_SIZE = 200;

    /**
     * 默认异步请求线程池最大队列长度
     */
    public final static Integer DEFAULT_MAX_QUEUE_SIZE = 100;

    /**
     * 异步线程计数
     */
    private final static AtomicInteger threadCount = new AtomicInteger(0);

    /**
     * 默认异步请求线程池拒绝策略
     *
     * @since 1.5.29
     */
    private final static AsyncAbortPolicy DEFAULT_ASYNC_REQUEST_REJECT_POLICY = new AsyncAbortPolicy();
    private static final Logger log = LoggerFactory.getLogger(AsyncThreadPools.class);

    /**
     * 创建Forest异步请求线程池
     *
     * @param configuration Forest配置对象
     * @return {@code ThreadPoolExecutor}对象实例
     * @since 1.5.29
     */
    private static ThreadPoolExecutor createAsyncThreadPool(final ForestConfiguration configuration, String asyncPoolName) {
        ForestConfiguration.MultiAsyncPoolConfig multiAsyncPoolConfig = configuration.getMultiAsyncPoolConfig().get(asyncPoolName);
        if (multiAsyncPoolConfig == null) {
            return createPool(configuration.getMaxAsyncThreadSize(), configuration.getMaxAsyncQueueSize(), asyncPoolName, configuration.getAsyncRejectPolicy());
        }
        return createPool(multiAsyncPoolConfig.getMaxAsyncThreadSize(), multiAsyncPoolConfig.getMaxAsyncQueueSize(), asyncPoolName, configuration.getAsyncRejectPolicy());
    }

    private static ThreadPoolExecutor createPool(Integer maxAsyncThreadSize, Integer maxQueueSize, String asyncPoolName, RejectedExecutionHandler rejectedExecutionHandler) {
        final int threadSize = maxAsyncThreadSize != null ? maxAsyncThreadSize : DEFAULT_MAX_THREAD_SIZE;
        final int queueSize = maxQueueSize == null ? DEFAULT_MAX_QUEUE_SIZE : maxQueueSize;
        final BlockingQueue<Runnable> queue = queueSize > 0 ? new LinkedBlockingQueue<>(queueSize) : new SynchronousQueue<>();
        log.info("[Forest] create async thread pool, poolName:{} maxThreadSize:{} maxQueueSize:{}", asyncPoolName, threadSize, queueSize);
        return new ThreadPoolExecutor(
                threadSize, threadSize,
                0, TimeUnit.MINUTES,
                queue,
                tf -> {
                    Thread thread = new Thread(tf, "forest-async-" + asyncPoolName + "-" + threadCount.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                },
                (rejectedExecutionHandler != null ? rejectedExecutionHandler : DEFAULT_ASYNC_REQUEST_REJECT_POLICY)::rejectedExecution
        );
    }

    public static ThreadPoolExecutor getOrCreate(ForestConfiguration configuration, String asyncPoolName) {
        ThreadPoolExecutor pool = configuration.asyncPools.get(asyncPoolName);
        if (pool == null) {
            synchronized (configuration.ASYNC_POOL_LOCK) {
                pool = configuration.asyncPools.get(asyncPoolName);
                if (pool == null) {
                    pool = createAsyncThreadPool(configuration, asyncPoolName);
                    configuration.asyncPools.put(asyncPoolName, pool);
                }
            }
        }
        return pool;
    }


    public static ThreadPoolExecutor get(ForestConfiguration configuration, String asyncPoolName) {
        return configuration.asyncPools.get(asyncPoolName);
    }
}
