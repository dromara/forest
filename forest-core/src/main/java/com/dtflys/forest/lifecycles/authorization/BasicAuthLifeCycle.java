package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.Base64Utils;
import com.dtflys.forest.utils.StringUtils;

/**
 * BasicAuth 注解的生命周期类
 */
public class BasicAuthLifeCycle implements MethodAnnotationLifeCycle<BasicAuth, Void> {

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {

    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        final String username = getAttributeAsString(request, "username");
        final String password = getAttributeAsString(request, "password");
        if (StringUtils.isNotEmpty(username) || StringUtils.isNotEmpty(password)) {
            request.authenticator(new com.dtflys.forest.auth.BasicAuth(username, password));
        }
        return true;
    }


    @Override
    public void onMethodInitialized(ForestMethod method, BasicAuth annotation) {

    }
}
