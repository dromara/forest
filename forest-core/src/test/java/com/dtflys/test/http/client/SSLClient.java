package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.SSLHostnameVerifier;
import com.dtflys.forest.annotation.SSLSocketFactoryBuilder;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.ssl.MyHostnameVerifier;
import com.dtflys.test.http.ssl.MySSLSocketFactoryBuilder;

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
            sslProtocol = "${sslProtocol?}"
    )
    ForestResponse<String> truestSSLGet(@Var("sslProtocol") String sslProtocol);

    @Get(url = "https://localhost:{port}/hello/user", keyStore = "ssl_client")
    ForestResponse<String> testConcurrent(@Query("id") int id);
}
