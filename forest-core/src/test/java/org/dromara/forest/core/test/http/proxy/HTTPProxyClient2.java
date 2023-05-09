package org.dromara.forest.core.test.http.proxy;

import org.dromara.forest.annotation.HTTPProxy;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.http.ForestRequest;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-17
 */
@HTTPProxy(source = MyHTTPProxySource.class)
public interface HTTPProxyClient2 {

    @Post("/")
    ForestRequest<String> sendHTTPProxySource(@Var("port") int port);

    @Post("/")
    @HTTPProxy(source = MyHTTPProxySource2.class)
    ForestRequest<String> sendHTTPProxySource2(@Var("port") int port);


}
