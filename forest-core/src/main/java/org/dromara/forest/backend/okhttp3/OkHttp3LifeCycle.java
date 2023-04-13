package org.dromara.forest.backend.okhttp3;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;

public class OkHttp3LifeCycle implements MethodAnnotationLifeCycle<OkHttp3, Object> {

    private final static String PARAM_KEY_OKHTTP3_PROVIDER = "__okhttp3_provider";

    @Override
    public void onMethodInitialized(ForestMethod method, OkHttp3 annotation) {
        Class<? extends OkHttpClientProvider> clazz = annotation.client();
        if (clazz != null) {
            OkHttpClientProvider provider = method.getConfiguration().getForestObject(clazz);
            method.setExtensionParameterValue(PARAM_KEY_OKHTTP3_PROVIDER, provider);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        Object provider = method.getExtensionParameterValue(PARAM_KEY_OKHTTP3_PROVIDER);
        if (provider != null) {
            request.setBackendClient(provider);
        }
    }
}
