package org.dromara.forest.core.test.http.address;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-17 2:07
 */
public interface AddressClient {

    @Post("/")
    @Address(host = "${0}", port = "${1}")
    ForestRequest<String> testHostPort(String host, int port);

    @Post("/")
    @Address(host = "${0}", port = "${1}", scheme = "https")
    ForestRequest<String> testHttpsHostPort(String host, int port);



    @Post("/")
    @Address(source = MyAddressSource.class)
    ForestRequest<String> testAddressSource(@Var("port") int port);

    @Post("/xxx")
    @Address(host = "{0}", port = "{1}", basePath = "{2}")
    ForestRequest<String> testBasePath(String host, int port, String basePath);

    @Post("/xxx")
    @Address(basePath = "http://localhost:{0}/aaa")
    ForestRequest<String> testBasePathOnly(int port);

}
