package com.dtflys.test.interceptor;

import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.interceptor.Interceptor;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-15 17:01
 */
public class SimpleInterceptor implements Interceptor {

    private final static Logger log = LoggerFactory.getLogger(SimpleInterceptor.class);

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        log.info("invoke Simple beforeExecute");
        request.setType(ForestRequestType.GET);
        request.addHeader(HttpHeaders.ACCEPT, "text/plain");
        request.addQuery("username", "foo");
        return proceed();
    }

    @Override
    public void onSuccess(ForestRequest request, ForestResponse response) {
        log.info("invoke Simple onSuccess");
        response.setResult("XX: " + response.getResult());
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
        log.info("invoke Simple afterExecute");
    }
}
