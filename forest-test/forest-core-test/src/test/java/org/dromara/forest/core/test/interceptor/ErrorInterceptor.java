package org.dromara.forest.core.test.interceptor;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;

public class ErrorInterceptor implements Interceptor {
    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
        response.setResult("{\"error\": true, \"interceptor\": true}");
    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }
}
