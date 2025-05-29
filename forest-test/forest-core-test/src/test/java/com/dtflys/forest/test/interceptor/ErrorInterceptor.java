package com.dtflys.forest.test.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;

public class ErrorInterceptor implements Interceptor {
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        response.setResult("{\"error\": true, \"interceptor\": true}");
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }
}
