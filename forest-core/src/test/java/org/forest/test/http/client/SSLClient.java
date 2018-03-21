package org.forest.test.http.client;

import org.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 19:39
 */
public interface SSLClient {

    @Request(
            url = "https://localhost:5000/hello/user",
            keyStore = "ssl_client"
    )
    String truestAllGet();

}
