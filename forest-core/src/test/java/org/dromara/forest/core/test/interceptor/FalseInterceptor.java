package org.dromara.forest.core.test.interceptor;

import com.alibaba.fastjson.JSON;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 18:26
 */
public class FalseInterceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(FalseInterceptor.class);

    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info("invoke False beforeExecute");
        Object[] args = request.getArguments();
        args[0] = "b";
        log.info("args: " + JSON.toJSONString(args));
        return false;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("invoke False onSuccess");
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("invoke False afterExecute");
    }
}
