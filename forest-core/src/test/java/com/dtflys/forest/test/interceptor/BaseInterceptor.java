package com.dtflys.forest.test.interceptor;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 18:17
 */
public class BaseInterceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(BaseInterceptor.class);

    @Override
    public boolean beforeExecute(ForestRequest request) {
        log.info("invoke Base beforeExecute");
        Object[] args = request.getArguments();
        log.info("args: " + JSON.toJSONString(args));
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("invoke Base onSuccess, data: " + data);
        response.setResult("Base: " + data);
    }

    @Override
    public byte[] onBodyEncode(ForestRequest request, ForestEncoder encoder, byte[] encodedData) {
        return Interceptor.super.onBodyEncode(request, encoder, encodedData);
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
    }


    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        log.info("invoke Base afterExecute");
    }
}
