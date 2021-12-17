package com.dtflys.forest.backend;

import java.text.MessageFormat;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class AsyncAbortPolicy extends ThreadPoolExecutor.AbortPolicy {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String threadName = Thread.currentThread().getName();
        String msg = "[Forest] Asynchronous thread pool is full!\n\t" +
                "Thread name: {0}, Max pool size: {1}, core pool size: {2}, Active pool size: {3}, Task count: {4}";
        String errMsg = MessageFormat.format(
                msg, threadName, e.getMaximumPoolSize(), e.getCorePoolSize(), e.getActiveCount(), e.getActiveCount());
        throw new RejectedExecutionException(errMsg);
    }
}
