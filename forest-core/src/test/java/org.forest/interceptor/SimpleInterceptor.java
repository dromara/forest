package org.forest.interceptor;

import org.forest.exceptions.ForestRuntimeException;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.http.TestGetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 17:01
 */
public class SimpleInterceptor implements Interceptor<String> {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info("invoke beforeExecute");
        return true;
    }

    @Override
    public void onSuccess(String data, ForestRequest request, ForestResponse response) {
        log.info("invoke onSuccess");
        response.setResult("XX: " + data);
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("invoke afterExecute");
    }
}
