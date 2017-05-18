package org.forest.interceptor;

import org.forest.exceptions.ForestRuntimeException;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.TestGetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 18:17
 */
public class Simple2Interceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info("invoke Simple2 beforeExecute");
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("invoke Simple2 onSuccess");
        response.setResult("YY: " + data);
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("invoke Simple2 afterExecute");
    }
}
