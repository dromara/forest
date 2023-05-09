package org.dromara.forest.retryer;

import org.dromara.forest.exceptions.ForestRetryException;
import org.dromara.forest.http.ForestRequest;

/**
 * 基于退避算法的重试器
 */
public class BackOffRetryer extends ForestRetryer {

    protected final ForestRequest request;

    protected long waitedTime;


    public BackOffRetryer(ForestRequest request) {
        super(request);
        this.request = request;
        this.waitedTime = 0;
    }

    @Override
    public void canRetry(ForestRetryException ex) throws Throwable {
        int currentCount = getCurrentRetryCount();
        int maxRetryCount = getMaxRetryCount();
        long maxRetryInterval = getMaxRetryInterval();
        if (currentCount >= maxRetryCount) {
            if (currentCount == 0) {
                throw ex.getCause() == null ? ex : ex.getCause();
            }
            throw ex;
        }
        long interval = nextInterval(currentCount);
        if (interval > maxRetryInterval) {
            interval = maxRetryInterval;
        }
        if (interval > 0) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw ex;
            }
        }
        this.waitedTime += interval;
        getAndIncrementCurrentRetryCount();
    }

    protected long nextInterval(int currentCount) {
        long maxRetryInterval = getMaxRetryInterval();
        long interval = (long) Math.pow(2.0, currentCount) * 1000;
        if (maxRetryInterval >= 0 && interval > maxRetryInterval) {
            return maxRetryInterval;
        }
        return interval;
    }



    public long getWaitedTime() {
        return waitedTime;
    }
}
