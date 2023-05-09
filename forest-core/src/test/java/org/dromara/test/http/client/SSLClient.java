package org.dromara.test.http.client;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.DataVariable;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Query;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.annotation.SSLHostnameVerifier;
import org.dromara.forest.annotation.SSLSocketFactoryBuilder;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.dromara.test.http.ssl.MyHostnameVerifier;
import org.dromara.test.http.ssl.MySSLSocketFactoryBuilder;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 19:39
 */
@BaseRequest(sslProtocol = "TLS")
public interface SSLClient {

    @Request(
            url = "https://127.0.0.1:{port}/hello/user",
            keyStore = "ssl_client"
    )
    String truestAllGet();

    @Request(
            url = "https://{0}:{port}/hello/user",
            keyStore = "ssl_client2"
    )
    String testHostVerifier(String domain);

    @Request(
            url = "https://{0}:{port}/hello/user",
            keyStore = "ssl_client2"
    )
    @SSLHostnameVerifier(MyHostnameVerifier.class)
    @SSLSocketFactoryBuilder(MySSLSocketFactoryBuilder.class)
    ForestRequest<String> testHostVerifier2(String domain);

    @Request(
            url = "https://localhost:{port}/hello/user",
            sslProtocol = "${sslProtocol}"
    )
    ForestResponse<String> truestSSLGet(@DataVariable("sslProtocol") String sslProtocol);

    @Get(url = "https://localhost:{port}/hello/user", keyStore = "ssl_client")
    ForestResponse<String> testConcurrent(@Query("id") int id);
}
