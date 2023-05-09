package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.BackendClient;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class BackendClientLifeCycle implements MethodAnnotationLifeCycle<BackendClient, Object> {

    private final static String PARAM_KEY_BACKEND_CLIENT_CACHE = "__backend_client_cache";

    @Override
    public void onMethodInitialized(ForestMethod method, BackendClient annotation) {
        Boolean cache = annotation.cache();
        method.setExtensionParameterValue(PARAM_KEY_BACKEND_CLIENT_CACHE, cache);
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Object cache = request.getMethod().getExtensionParameterValue(PARAM_KEY_BACKEND_CLIENT_CACHE);
        if (cache != null && cache instanceof Boolean) {
            request.cacheBackendClient((Boolean) cache);
        }
    }


}
