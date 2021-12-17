package com.dtflys.test.http.proxy;

import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

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
    @HTTPProxy(source = MyHTTPProxySource.class)
    ForestRequest<String> sendHTTPProxySource(@Var("port") int port);
}
