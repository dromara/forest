package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJoinpoint;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.Base64Utils;
import com.dtflys.forest.utils.StringUtils;

public class BasicAuthLifeCycle implements MethodAnnotationLifeCycle<BasicAuth, Object> {

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {

    }

    @Override
    public ForestJoinpoint beforeExecute(ForestRequest request) {
        final String username = (String) getAttribute(request, "username");
        final String password = (String) getAttribute(request, "password");
        if (StringUtils.isNotEmpty(username) || StringUtils.isNotEmpty(password)) {
            request.authenticator(new com.dtflys.forest.auth.BasicAuth(username, password));
        }
        return proceed();
    }


    @Override
    public void onMethodInitialized(ForestMethod method, BasicAuth annotation) {

    }
}
