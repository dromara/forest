package com.dtflys.test.http.address;

import com.dtflys.forest.annotation.Address;
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
    ForestRequest<String> sendHostPort(String host, int port);

    @Post("/")
    @Address(source = MyAddressSource.class)
    ForestRequest<String> sendAddressSource(@Var("port") int port);


}
