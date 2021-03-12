package com.dtflys.forest.lifecycles.proxy;

import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.mapping.MappingTemplate;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.utils.StringUtils;

/**
 * HTTP正向代理生命周期类
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-BETA5
 */
public class HTTPProxyLifeCycle implements MethodAnnotationLifeCycle<HTTPProxy, Object> {

    @Override
    public void onInvokeMethod(ForestRequest request, ForestMethod method, Object[] args) {
        String hostStr = (String) getAttribute(request, "host");
        String portStr = (String) getAttribute(request, "port");
        String usernameStr = (String) getAttribute(request, "username");
        String passwordStr = (String) getAttribute(request, "password");

        MappingTemplate hostTemplate = method.makeTemplate(hostStr);
        MappingTemplate portTemplate = method.makeTemplate(portStr);
        MappingTemplate usernameTemplate = method.makeTemplate(usernameStr);
        MappingTemplate passwordTemplate = method.makeTemplate(passwordStr);

        addAttribute(request, "host_temp", hostTemplate);
        addAttribute(request, "port_temp", portTemplate);
        addAttribute(request, "username_temp", usernameTemplate);
        addAttribute(request, "password_temp", passwordTemplate);
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        MappingTemplate hostTemplate = (MappingTemplate) getAttribute(request, "host_temp");
        MappingTemplate portTemplate = (MappingTemplate) getAttribute(request, "port_temp");
        MappingTemplate usernameTemplate = (MappingTemplate) getAttribute(request, "username_temp");
        MappingTemplate passwordTemplate = (MappingTemplate) getAttribute(request, "password_temp");

        Object[] args = request.getArguments();
        String host = hostTemplate.render(args);
        String portStr = portTemplate.render(args);
        String username = null, password = null;

        if (usernameTemplate != null) {
            username = usernameTemplate.render(args);
        }
        if (passwordTemplate != null) {
            password = passwordTemplate.render(args);
        }

        int port = 80;
        if (StringUtils.isBlank(host)) {
            throw new ForestRuntimeException("[Forest] Proxy host cannot be empty!");
        }
        if (StringUtils.isNotBlank(portStr)) {
            try {
                port = Integer.parseInt(portStr);
            } catch (Throwable th) {
            }
        }
        ForestProxy proxy = new ForestProxy(host, port);
        if (StringUtils.isNotEmpty(username)) {
            proxy.setUsername(username);
        }
        if (StringUtils.isNotEmpty(password)) {
            proxy.setPassword(password);
        }
        request.setProxy(proxy);
        return true;
    }


    @Override
    public void onMethodInitialized(ForestMethod method, HTTPProxy annotation) {

    }
}
