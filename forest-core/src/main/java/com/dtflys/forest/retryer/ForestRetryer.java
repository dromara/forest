package com.dtflys.forest.retryer;

import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Forest请求重试器
 */
public abstract class ForestRetryer {

    protected final ForestRequest request;

    private AtomicInteger currentRetryCount = new AtomicInteger(0);

    protected ForestRetryer(ForestRequest request) {
        this.request = request;
    }

    public ForestRequest getRequest() {
        return request;
    }

    public int getMaxRetryCount() {
        return request.getRetryCount();
    }

    public int getCurrentRetryCount() {
        return currentRetryCount.get();
    }

    public int getAndIncrementCurrentRetryCount() {
        return currentRetryCount.getAndIncrement();
    }




    public abstract void canRetry(ForestRetryException ex) throws Throwable;

}
