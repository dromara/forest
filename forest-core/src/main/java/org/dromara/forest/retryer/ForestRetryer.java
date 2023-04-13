package org.dromara.forest.retryer;

import org.dromara.forest.exceptions.ForestRetryException;
import org.dromara.forest.http.ForestRequest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Forest请求重试器
 *
 * @author gongjun [dt_flys@hotmail.com]
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

    public long getMaxRetryInterval() {
        return request.getMaxRetryInterval();
    }

    public int getCurrentRetryCount() {
        return currentRetryCount.get();
    }

    public int getAndIncrementCurrentRetryCount() {
        return currentRetryCount.getAndIncrement();
    }

    public abstract void canRetry(ForestRetryException ex) throws Throwable;

}
