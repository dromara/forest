package com.dtflys.test.http.proxy;

import com.dtflys.forest.callback.HTTPProxySource;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author caihongming
 * @version v1.0
 * @since 1.5.17
 */
public class MyHTTPProxySource implements HTTPProxySource {

    @Override
    public ForestProxy getProxy(ForestRequest request) {
        return new ForestProxy("127.0.0.1", (Integer) request.variableValue("port"));
    }
}
