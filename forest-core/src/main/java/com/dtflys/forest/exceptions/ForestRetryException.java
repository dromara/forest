package com.dtflys.forest.exceptions;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.http.ForestRequest;

public class ForestRetryException extends ForestRuntimeException {

    private final int maxRetryCount;
    private final int currentRetryCount;
    private final AbstractHttpExecutor executor;
    private final ForestRequest request;

    public ForestRetryException(Throwable cause, AbstractHttpExecutor executor, ForestRequest request, int maxRetryCount, int currentRetryCount) {
        super(cause);
        this.executor = executor;
        this.request = request;
        this.maxRetryCount = maxRetryCount;
        this.currentRetryCount = currentRetryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public int getCurrentRetryCount() {
        return currentRetryCount;
    }

    public AbstractHttpExecutor getExecutor() {
        return executor;
    }

    public ForestRequest getRequest() {
        return request;
    }
}
