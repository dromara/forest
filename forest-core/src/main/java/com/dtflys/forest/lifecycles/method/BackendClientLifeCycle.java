package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.annotation.BackendClient;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class BackendClientLifeCycle implements MethodAnnotationLifeCycle<BackendClient, Void> {

    private final static String PARAM_KEY_BACKEND_CLIENT_CACHE = "__backend_client_cache";

    @Override
    public void onMethodInitialized(ForestMethod method, BackendClient annotation) {
        final Boolean cache = annotation.cache();
        method.setExtensionParameterValue(PARAM_KEY_BACKEND_CLIENT_CACHE, cache);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object cache = request.getMethod().getExtensionParameterValue(PARAM_KEY_BACKEND_CLIENT_CACHE);
        if (cache != null && cache instanceof Boolean) {
            request.cacheBackendClient((Boolean) cache);
        }
    }


}
