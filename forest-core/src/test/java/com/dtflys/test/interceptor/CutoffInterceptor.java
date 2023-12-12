package com.dtflys.test.interceptor;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 18:26
 */
public class CutoffInterceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(CutoffInterceptor.class);

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        log.info("invoke False beforeExecute");
        Object[] args = request.getArguments();
        args[0] = "b";
        log.info("args: " + JSON.toJSONString(args));
        return cutoff();
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
