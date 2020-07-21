package com.dtflys.forest.retryer;

import com.dtflys.forest.http.ForestRequest;

public interface Retryer {

    void doRetry(ForestRequest request, int retryCount);

}
