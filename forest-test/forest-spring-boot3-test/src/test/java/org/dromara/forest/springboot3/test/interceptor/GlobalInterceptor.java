package org.dromara.forest.springboot3.test.interceptor;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-07-13 15:17
 */
@Component
public class GlobalInterceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(GlobalInterceptor.class);

    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info("invoke Global beforeExecute");
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("invoke Global onSuccess");
        response.setResult("Global: " + data);
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("invoke global afterExecute");
    }
}
