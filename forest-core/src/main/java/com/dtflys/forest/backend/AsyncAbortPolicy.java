package com.dtflys.forest.backend;

import com.dtflys.forest.exceptions.ForestAsyncAbortException;

import java.text.MessageFormat;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class AsyncAbortPolicy extends ThreadPoolExecutor.AbortPolicy {

    @Override
    public void rejectedExecution(Runnable task, ThreadPoolExecutor pool) {
        throw new ForestAsyncAbortException(task, pool);
    }
}
