package com.dtflys.forest.lifecycles.intercetpor;

import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.Base64Utils;

public class IgnoreAllInterceptorsLifeCycle implements MethodAnnotationLifeCycle<BasicAuth, Object> {

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {

    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String username = (String) getAttribute(request, "username");
        String password = (String) getAttribute(request, "password");
        String basic = "Basic " + Base64Utils.encode(username + ":" + password);
        request.addHeader("Authorization", basic);
        return true;
    }


    @Override
    public void onMethodInitialized(ForestMethod method, BasicAuth annotation) {

    }
}
