package com.dtflys.test.http.hostaddr;

import com.dtflys.forest.annotation.HostAddress;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-17 2:07
 */
public interface HostAddressClient {

    @Post("/")
    @HostAddress(host = "${0}", port = "${1}")
    ForestRequest<String> send(String host, int port);

}
