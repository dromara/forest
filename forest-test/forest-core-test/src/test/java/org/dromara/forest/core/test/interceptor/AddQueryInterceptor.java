package org.dromara.forest.core.test.interceptor;

import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.forest.interceptor.Interceptor;

public class AddQueryInterceptor implements Interceptor {

    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        Integer port = (Integer) request.getConfiguration().getVariableValue("port");
        request.setUrl("http://localhost:" + port + "/hello/user?username=foo");
        request.addQuery("password", "bar");
        return true;
    }
}
