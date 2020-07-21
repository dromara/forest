package com.dtflys.forest.retryer;

import com.dtflys.forest.http.ForestRequest;

public class DefaultRetryer implements Retryer {

    private int retryCount;

    private long retryInterval;

    private long maxRetryInterval;

    @Override
    public void doRetry(ForestRequest request, int retryCount) {

    }

}
