package com.dtflys.test.http.backend;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.backend.httpclient.HttpClient;
import com.dtflys.forest.backend.okhttp3.OkHttp3;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

@Address(host = "localhost", port = "${port}")
public interface BackendClient {

    @Backend("httpclient")
    @Post("/")
    ForestRequest<String> testHttpclient(@Var("port") int port);

    @HttpClient
    @Post("/")
    ForestRequest<String> testHttpclient_2(@Var("port") int port);

    @Backend("okhttp3")
    @Post("/")
    ForestRequest<String> testOkHttp3(@Var("port") int port);

    @OkHttp3
    @Post("/")
    ForestRequest<String> testOkHttp3_2(@Var("port") int port);

    @Backend("${1}")
    @Post("/")
    ForestRequest<String> testVariableBackend(@Var("port") int port, String backendName);



}
