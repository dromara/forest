package com.dtflys.forest.lifecycles.proxy;

import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.callback.HTTPProxySource;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestProxyType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.HeaderUtils;
import com.dtflys.forest.utils.StringUtils;

import java.util.Arrays;

/**
 * HTTP正向代理生命周期类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA5
 */
public class HTTPProxyLifeCycle implements MethodAnnotationLifeCycle<HTTPProxy> {

    private final static String PARAM_KEY_HTTP_PROXY_SOURCE = "__http_proxy_source";
    private final static String PARAM_KEY_HTTP_PROXY = "__http_proxy";

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        final ForestProxyType type = (ForestProxyType) getAttribute(request, "type");
        final String hostStr = (String) getAttribute(request, "host");
        String portStr = (String) getAttribute(request, "port");
        final String usernameStr = (String) getAttribute(request, "username");
        final String passwordStr = (String) getAttribute(request, "password");
        final String[] headersStr = (String[]) getAttribute(request, "headers");

        final MappingTemplate hostTemplate = MappingTemplate.annotation(HTTPProxy.class, "host", hostStr);
        final MappingTemplate portTemplate = MappingTemplate.annotation(HTTPProxy.class, "port", portStr);
        final MappingTemplate usernameTemplate = MappingTemplate.annotation(HTTPProxy.class, "username", usernameStr);
        final MappingTemplate passwordTemplate = MappingTemplate.annotation(HTTPProxy.class, "password", passwordStr);
        final Object httpProxySource = request.getMethod().getExtensionParameterValue(PARAM_KEY_HTTP_PROXY_SOURCE);
        final MappingTemplate[] headersTemplates = Arrays.stream(headersStr)
                .map(headerStr -> MappingTemplate.annotation( HTTPProxy.class, "headers", headerStr))
                .toArray(MappingTemplate[]::new);

        final String host = hostTemplate.render(request);

        String username = null, password = null;

        if (usernameTemplate != null) {
            username = usernameTemplate.render(request);
        }
        if (passwordTemplate != null) {
            password = passwordTemplate.render(request);
        }

        int port = 80;
        if (StringUtils.isBlank(host)) {
            if (httpProxySource != null && httpProxySource instanceof HTTPProxySource) {
                request.setProxy(((HTTPProxySource) httpProxySource).getProxy(request));
                return;
            }
            throw new ForestRuntimeException("[Forest] Proxy host cannot be empty!");
        }
        if (StringUtils.isNotBlank(portStr)) {
            portStr = portTemplate.render(request);
            try {
                port = Integer.parseInt(portStr);
            } catch (Throwable th) {
            }
        }
        final ForestProxy proxy = new ForestProxy(type, host, port);
        if (StringUtils.isNotEmpty(username)) {
            proxy.setUsername(username);
        }
        if (StringUtils.isNotEmpty(password)) {
            proxy.setPassword(password);
        }
        if (headersTemplates != null && headersTemplates.length > 0) {
            HeaderUtils.addHeaders(request, proxy, headersTemplates, args);
        }
        request.setProxy(proxy);

    }

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        return proceed();
    }


    @Override
    public void onMethodInitialized(ForestMethod method, HTTPProxy annotation) {
        final Class<? extends HTTPProxySource> clazz = annotation.source();
        if (clazz != null && !clazz.isInterface()) {
            HTTPProxySource proxySource = method.config().getForestObject(clazz);
            method.setExtensionParameterValue(PARAM_KEY_HTTP_PROXY_SOURCE, proxySource);
        }
        method.setExtensionParameterValue(PARAM_KEY_HTTP_PROXY, annotation);
    }
}
