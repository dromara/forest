package org.dromara.forest.forestspringboot3example.interceptors;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestHeaderMap;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ApiClientInterceptor implements Interceptor {

    private final Log log = LogFactory.getLog(ApiClientInterceptor.class);

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String accessToken = "111111111";
        request.addHeader("accessToken", accessToken);
        log.info("accessToken = " + accessToken);
        return true;
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {
        log.info("invoke Simple onSuccess");
    }

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        log.info("invoke Simple onError");
    }

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        ForestHeaderMap headers = request.getHeaders();
        System.out.println(headers.getValues());
        log.info("invoke Simple afterExecute");
    }
}
