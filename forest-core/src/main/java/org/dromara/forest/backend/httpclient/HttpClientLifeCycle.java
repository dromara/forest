package org.dromara.forest.backend.httpclient;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;

public class HttpClientLifeCycle implements MethodAnnotationLifeCycle<HttpClient, Object> {

    private final static String PARAM_KEY_HTTPCLIENT_PROVIDER = "__httpclient_provider";

    @Override
    public void onMethodInitialized(ForestMethod method, HttpClient annotation) {
        final Class<? extends HttpClientProvider> clazz = annotation.client();
        if (clazz != null) {
            final HttpClientProvider provider = method.getConfiguration().getForestObject(clazz);
            method.setExtensionParameterValue(PARAM_KEY_HTTPCLIENT_PROVIDER, provider);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object provider = method.getExtensionParameterValue(PARAM_KEY_HTTPCLIENT_PROVIDER);
        if (provider != null) {
            request.setBackendClient(provider);
        }
    }
}
