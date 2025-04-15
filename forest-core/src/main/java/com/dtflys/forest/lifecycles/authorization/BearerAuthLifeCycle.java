package com.dtflys.forest.lifecycles.authorization;

import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

public class BearerAuthLifeCycle implements MethodAnnotationLifeCycle<BasicAuth, Object> {

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {

    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        final String token = getAttributeAsString(request, "token");
        if (StringUtils.isNotEmpty(token)) {
            request.authenticator(new com.dtflys.forest.auth.BearerAuth(token));
        }
        return true;
    }


    @Override
    public void onMethodInitialized(ForestMethod method, BasicAuth annotation) {

    }
}
