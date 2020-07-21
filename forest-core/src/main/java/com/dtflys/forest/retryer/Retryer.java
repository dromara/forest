package com.dtflys.forest.retryer;

import com.dtflys.forest.exceptions.ForestRetryException;

public interface Retryer {

    void doRetry(ForestRetryException ex) throws Throwable;

}
