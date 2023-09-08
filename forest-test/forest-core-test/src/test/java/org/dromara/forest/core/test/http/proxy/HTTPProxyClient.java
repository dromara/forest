package org.dromara.forest.core.test.http.proxy;

import org.dromara.forest.annotation.HTTPProxy;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.SocksProxy;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.http.ForestProxyType;
import org.dromara.forest.http.ForestRequest;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-12-17
 */
public interface HTTPProxyClient {

    @Post("/")
    @HTTPProxy(host = "${0}", port = "${1}")
    ForestRequest<String> sendHostPort(String host, int port);

    @Post("/")
    @HTTPProxy(type = ForestProxyType.SOCKS, host = "${0}", port = "${1}")
    ForestRequest<String> sendHostPortSocks(String host, int port);

    @Post("/")
    @SocksProxy(host = "${0}", port = "${1}")
    ForestRequest<String> sendHostPortSocks2(String host, int port);


    @Post("/")
    @HTTPProxy(source = MyHTTPProxySource.class)
    ForestRequest<String> sendHTTPProxySource(@Var("port") int port);
}
