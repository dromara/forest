package com.dtflys.forest.retryer;

import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.http.ForestRequest;

/**
 * 空重试器，该重试器不会错任何重试动作
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-09 20:17
 */
public class NoneRetryer extends ForestRetryer {

    public NoneRetryer(ForestRequest request) {
        super(request);
    }

    @Override
    public void canRetry(ForestRetryException ex) throws Throwable {
        throw ex.getCause();
    }
}
