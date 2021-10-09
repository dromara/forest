package com.dtflys.test.http.backend;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.backend.httpclient.HttpClient;
import com.dtflys.forest.backend.okhttp3.OkHttp3;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

@OkHttp3
@Address(host = "localhost", port = "${port}")
public interface BackendClient2 {

    @Post("/")
    ForestRequest<String> testBaseBackend(@Var("port") int port);

    @HttpClient
    @Post("/")
    ForestRequest<String> testMethodBackend(@Var("port") int port);

}
