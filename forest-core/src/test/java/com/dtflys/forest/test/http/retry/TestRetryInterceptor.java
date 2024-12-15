package com.dtflys.forest.test.http.retry;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;

public class TestRetryInterceptor implements Interceptor<Object> {

    /**
     * 在请重试前调用 onRetry 回调函数
     *
     * @param request Forest请求对象
     * @param response Forest响应对象
     */
    @Override
    public void onRetry(ForestRequest request, ForestResponse response) {
        // 将当前重试次数添加到 Forest 请求对象的附件中
        request.addAttachment("retry-interceptor", request.getCurrentRetryCount());
        request.addQuery("a", request.getCurrentRetryCount());
    }
}
