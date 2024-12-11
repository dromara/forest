package com.dtflys.forest.test.http.proxy;

import com.dtflys.forest.callback.HTTPProxySource;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-17
 */
public class MyHTTPProxySource2 implements HTTPProxySource {

    @Override
    public ForestProxy getProxy(ForestRequest request) {
        return new ForestProxy("localhost", (Integer) request.variableValue("port"));
    }
}
