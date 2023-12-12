package com.dtflys.forest.backend.okhttp3;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;

public class OkHttp3LifeCycle implements MethodAnnotationLifeCycle<OkHttp3> {

    private final static String PARAM_KEY_OKHTTP3_PROVIDER = "__okhttp3_provider";

    @Override
    public void onMethodInitialized(ForestMethod method, OkHttp3 annotation) {
        final Class<? extends OkHttpClientProvider> clazz = annotation.client();
        if (clazz != null) {
            final OkHttpClientProvider provider = method.getConfiguration().getForestObject(clazz);
            method.setExtensionParameterValue(PARAM_KEY_OKHTTP3_PROVIDER, provider);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object provider = method.getExtensionParameterValue(PARAM_KEY_OKHTTP3_PROVIDER);
        if (provider != null) {
            request.setBackendClient(provider);
        }
    }
}
