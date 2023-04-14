package org.dromara.forest.test.http.backend;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Backend;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.backend.httpclient.HttpClient;
import org.dromara.forest.backend.okhttp3.OkHttp3;
import org.dromara.forest.http.ForestRequest;

@Address(host = "localhost", port = "{port}")
public interface BackendClient {

    @Backend("httpclient")
    @Post
    ForestRequest<String> testHttpclient(@Var("port") int port);

    @HttpClient
    @Post
    ForestRequest<String> testHttpclient_2(@Var("port") int port);

    @Backend("okhttp3")
    @Post
    ForestRequest<String> testOkHttp3(@Var("port") int port);

    @OkHttp3
    @Post
    ForestRequest<String> testOkHttp3_2(@Var("port") int port);

    @Backend("{1}")
    @Post
    ForestRequest<String> testVariableBackend(@Var("port") int port, String backendName);



}
