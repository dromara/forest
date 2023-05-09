package org.dromara.forest.backend;

import org.dromara.forest.exceptions.ForestAsyncAbortException;

import java.util.concurrent.ThreadPoolExecutor;

public class AsyncAbortPolicy extends ThreadPoolExecutor.AbortPolicy {

    @Override
    public void rejectedExecution(Runnable task, ThreadPoolExecutor pool) {
        throw new ForestAsyncAbortException(task, pool);
    }
}
