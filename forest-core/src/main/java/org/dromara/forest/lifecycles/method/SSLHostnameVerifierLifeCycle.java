package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.SSLHostnameVerifier;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;

import javax.net.ssl.HostnameVerifier;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class SSLHostnameVerifierLifeCycle implements MethodAnnotationLifeCycle<SSLHostnameVerifier, Object> {

    private final static String PARAM_KEY_HOSTNAME_VERIFIER = "__hostname_verifier";

    @Override
    public void onMethodInitialized(ForestMethod method, SSLHostnameVerifier annotation) {
        final Class<? extends HostnameVerifier> clazz = annotation.value();
        final HostnameVerifier hostnameVerifier = method.getConfiguration().getForestObjectFactory().getObject(clazz);
        if (hostnameVerifier != null) {
            method.setExtensionParameterValue(PARAM_KEY_HOSTNAME_VERIFIER, hostnameVerifier);
        }
    }

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final Object hostnameVerifier = request.getMethod().getExtensionParameterValue(PARAM_KEY_HOSTNAME_VERIFIER);
        if (hostnameVerifier != null && hostnameVerifier instanceof HostnameVerifier) {
            request.hostnameVerifier((HostnameVerifier) hostnameVerifier);
        }
    }


}
