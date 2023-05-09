package org.dromara.forest.core.test.http.proxy;

import org.dromara.forest.callback.HTTPProxySource;
import org.dromara.forest.http.ForestProxy;
import org.dromara.forest.http.ForestRequest;

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
