package com.dtflys.test.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.interceptor.Interceptor;

public class AddQueryInterceptor implements Interceptor {

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        Integer port = (Integer) request.getConfiguration().getVariableValue("port");
        request.setUrl("http://localhost:" + port + "/hello/user?username=foo");
        request.addQuery("password", "bar");
        return proceed();
    }
}
