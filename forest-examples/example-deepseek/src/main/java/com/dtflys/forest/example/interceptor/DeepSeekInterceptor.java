package com.dtflys.forest.example.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ResponseResult;
import com.dtflys.forest.interceptor.SSEInterceptor;

public class DeepSeekInterceptor implements SSEInterceptor {

    @Override
    public ResponseResult onResponse(ForestRequest request, ForestResponse response) {
        if (response.isError()) {
            System.out.println("服务端繁忙，请稍后再试");
            return success();
        }
        return proceed();
    }

}
