package com.dtflys.forest.lifecycles;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.AnnotationLifeCycle;
import com.dtflys.forest.utils.Base64Utils;

public class BasicAuthLifeCycle implements AnnotationLifeCycle<BasicAuth, Object> {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String username = (String) getAttribute(request, "username");
        String password = (String) getAttribute(request, "password");
        String basic = "Basic " + Base64Utils.encode(username + ":" + password);
        request.addHeader("Authorization", basic);
        return true;
    }



    @Override
    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {

    }

    @Override
    public void onSuccess(Object data, ForestRequest request, ForestResponse response) {

    }
}
