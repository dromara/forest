package com.dtflys.forest.test.interceptor;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.dtflys.forest.interceptor.ResponseResult;

public class EmptyResultInterceptor implements Interceptor<Object> {

    @Override
    public ResponseResult onResponse(ForestRequest request, ForestResponse response) {
        return success(null);
    }

}
