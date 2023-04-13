package org.dromara.forest.test.http.proxy;

import org.dromara.forest.callback.HTTPProxySource;
import org.dromara.forest.http.ForestProxy;
import org.dromara.forest.http.ForestRequest;

/**
 * @author caihongming
 * @since 1.5.17
 */
public class MyHTTPProxySource implements HTTPProxySource {

    @Override
    public ForestProxy getProxy(ForestRequest request) {
        return new ForestProxy("127.0.0.1", (Integer) request.variableValue("port"));
    }
}
