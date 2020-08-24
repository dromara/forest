package com.dtflys.forest.retryer;

import com.dtflys.forest.exceptions.ForestRetryException;

/**
 * Forest请求重试器
 */
public interface Retryer {

    void canRetry(ForestRetryException ex) throws Throwable;

}
