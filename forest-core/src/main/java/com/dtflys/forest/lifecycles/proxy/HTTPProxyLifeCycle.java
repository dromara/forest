package com.dtflys.forest.lifecycles.proxy;

import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
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

    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        String host = (String) getAttribute(request, "host");
        String portStr = (String) getAttribute(request, "port");
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
        request.setProxy(proxy);
        return true;
    }


    @Override
    public void onMethodInitialized(ForestMethod method, HTTPProxy annotation) {

    }
}
