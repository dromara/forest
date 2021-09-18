package com.dtflys.test.http.backend;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Backend;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

@Backend("okhttp3")
@Address(host = "localhost", port = "${port}")
public interface BackendClient2 {

    @Post("/")
    ForestRequest<String> testBaseBackend( @Var("port") int port);

    @Backend("httpclient")
    @Post("/")
    ForestRequest<String> testMethodBackend( @Var("port") int port);


}
