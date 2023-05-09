package org.dromara.forest.config;

import org.dromara.forest.backend.AsyncAbortPolicy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    /**
     * 创建Forest异步请求线程池
     *
     * @param configuration Forest配置对象
     * @return {@code ThreadPoolExecutor}对象实例
     * @since 1.5.29
     */
    private static ThreadPoolExecutor createAsyncThreadPool(final ForestConfiguration configuration) {
        final Integer maxAsyncThreadSize = configuration.getMaxAsyncThreadSize();
        final Integer maxQueueSize = configuration.getMaxAsyncQueueSize();
        final int threadSize = maxAsyncThreadSize != null ? maxAsyncThreadSize : DEFAULT_MAX_THREAD_SIZE;
        final int queueSize = maxQueueSize == null ? DEFAULT_MAX_QUEUE_SIZE : maxQueueSize;
        final BlockingQueue queue = queueSize > 0 ? new LinkedBlockingQueue<>(queueSize) : new SynchronousQueue<>();
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(
                threadSize, threadSize,
                0, TimeUnit.MINUTES,
                queue,
                tf -> {
                    Thread thread = new Thread(tf, "forest-async-" + threadCount.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                },
                (r, executor) -> (configuration.getAsyncRejectPolicy() != null ?
                        configuration.getAsyncRejectPolicy() : DEFAULT_ASYNC_REQUEST_REJECT_POLICY)
                        .rejectedExecution(r, executor)
        );
        return pool;
    }

    public static ThreadPoolExecutor getOrCreate(ForestConfiguration configuration) {
        ThreadPoolExecutor pool = configuration.asyncPool;
        if (pool == null) {
            synchronized (configuration.ASYNC_POOL_LOCK) {
                pool = configuration.asyncPool;
                if (pool == null) {
                    pool = createAsyncThreadPool(configuration);
                    configuration.asyncPool = pool;
                }
            }
        }
        return pool;
    }


    public static ThreadPoolExecutor get(ForestConfiguration configuration) {
        return configuration.asyncPool;
    }
}
