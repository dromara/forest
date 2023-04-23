package com.dtflys.test.http.address;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

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
