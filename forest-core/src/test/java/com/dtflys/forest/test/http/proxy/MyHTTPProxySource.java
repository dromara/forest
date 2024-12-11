package com.dtflys.forest.test.http.proxy;

import com.dtflys.forest.callback.HTTPProxySource;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author caihongming
 * @since 1.5.17
 */
public class MyHTTPProxySource implements HTTPProxySource {

    @Override
    public ForestProxy getProxy(ForestRequest request) {
        return ForestProxy.http("127.0.0.1", (Integer) request.variableValue("port"));
    }
}
