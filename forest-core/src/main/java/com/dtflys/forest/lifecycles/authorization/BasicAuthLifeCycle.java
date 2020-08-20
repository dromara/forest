package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.reflection.MethodAnnotationLifeCycle;
import com.dtflys.forest.utils.Base64Utils;

public class BasicAuthLifeCycle implements MethodAnnotationLifeCycle<BasicAuth, Object> {

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String username = (String) getAttribute(request, "username");
        String password = (String) getAttribute(request, "password");
        String basic = "Basic " + Base64Utils.encode(username + ":" + password);
        request.addHeader("Authorization", basic);
        return true;
    }


}
