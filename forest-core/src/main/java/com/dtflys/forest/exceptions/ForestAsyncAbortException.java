package com.dtflys.forest.exceptions;

import com.dtflys.forest.http.ForestRequest;

import java.text.MessageFormat;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Forest异步请求中断异常
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.22
 */
public class ForestAsyncAbortException extends ForestPoolException {

    private final Runnable task;

    private final ThreadPoolExecutor pool;

    public ForestAsyncAbortException(Runnable task, ThreadPoolExecutor pool) {
        super(null, message(task, pool));
        this.task = task;
        this.pool = pool;
    }

    public Runnable getTask() {
        return task;
    }

    public ThreadPoolExecutor getPool() {
        return pool;
    }

    private static String message(Runnable task, ThreadPoolExecutor pool) {
        String threadName = Thread.currentThread().getName();
        String msg = "[Forest] Asynchronous thread pool is full! " +
                "[Thread name: {0}, Max pool size: {1}, core pool size: {2}, Active pool size: {3}, Task count: {4}]";
        return MessageFormat.format(
                msg, threadName, pool.getMaximumPoolSize(), pool.getCorePoolSize(), pool.getActiveCount(), pool.getActiveCount());

    }
}
