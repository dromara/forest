package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 19:39
 */
public interface SSLClient {

    @Request(
            url = "https://localhost:5555/hello/user",
            keyStore = "ssl_client"
    )
    String truestAllGet();

}
