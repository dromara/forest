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
@HTTPProxy(source = MyHTTPProxySource.class)
public interface HTTPProxyClient2 {

    @Post("/")
    ForestRequest<String> sendHTTPProxySource(@Var("port") int port);

    @Post("/")
    @HTTPProxy(source = MyHTTPProxySource2.class)
    ForestRequest<String> sendHTTPProxySource2(@Var("port") int port);


}
