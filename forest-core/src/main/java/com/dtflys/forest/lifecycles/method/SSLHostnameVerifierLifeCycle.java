package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.SSLHostnameVerifier;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;

import javax.net.ssl.HostnameVerifier;

/**
 * Forest后端框架注解的生命周期类
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
public class SSLHostnameVerifierLifeCycle implements MethodAnnotationLifeCycle<SSLHostnameVerifier, Void> {

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
