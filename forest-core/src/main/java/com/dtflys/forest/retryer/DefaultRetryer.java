package com.dtflys.forest.retryer;

import com.dtflys.forest.exceptions.ForestRetryException;

public class DefaultRetryer implements Retryer {

    private final int retryCount;

    private final long retryInterval;

    private final long maxRetryInterval;

    private final long intervalInc;

    private long waitTime;

    public DefaultRetryer(int retryCount, long retryInterval, long maxRetryInterval) {
        this.retryCount = retryCount;
        this.maxRetryInterval = maxRetryInterval;
        if (retryInterval < 0) {
            this.retryInterval = 0;
        } else {
            this.retryInterval = retryInterval;
        }
        if (retryCount < 2) {
            this.intervalInc = 0;
        } else if (maxRetryInterval < retryInterval) {
             this.intervalInc = 0;
         } else {
            this.intervalInc = (maxRetryInterval - retryInterval) / (retryCount - 1);
        }
    }

    @Override
    public void doRetry(ForestRetryException ex) throws Throwable {
        int currentCount = ex.getCurrentRetryCount();
        if (currentCount >= retryCount) {
            throw ex.getCause();
        }

    }

}
