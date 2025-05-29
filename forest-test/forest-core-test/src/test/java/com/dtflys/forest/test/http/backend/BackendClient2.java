package com.dtflys.forest.test.http.backend;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.backend.httpclient.HttpClient;
import com.dtflys.forest.backend.okhttp3.OkHttp3;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

@HttpClient
@Address(host = "localhost", port = "${port}")
public interface BackendClient2 {

    @Post("/")
    ForestRequest<String> testBaseBackend(@Var("port") int port);

    @OkHttp3
    @Post("/")
    ForestRequest<String> testMethodBackend(@Var("port") int port);

}
