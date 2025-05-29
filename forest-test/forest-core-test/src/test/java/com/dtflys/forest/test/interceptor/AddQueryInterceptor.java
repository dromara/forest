package com.dtflys.forest.test.interceptor;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.ForestInterceptor;
import com.dtflys.forest.interceptor.Interceptor;

public class AddQueryInterceptor implements ForestInterceptor {

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }


    @Override
    public boolean beforeExecute(ForestRequest request) {
        Integer port = (Integer) request.getConfiguration().getVariableValue("port");
        request.setUrl("http://localhost:" + port + "/hello/user?username=foo");
        request.addQuery("password", "bar");
        return true;
    }
}
