package org.dromara.test.interceptor;

import com.alibaba.fastjson.JSON;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 18:17
 */
public class BaseErrorInterceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(BaseErrorInterceptor.class);

    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info("invoke Base Error beforeExecute");
        Object[] args = request.getArguments();
        log.info("args: " + JSON.toJSONString(args));
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        response.setResult("Base OnError is OK");
        throw new RuntimeException("xxxx");
    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("invoke Base afterExecute");
    }
}
