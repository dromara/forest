package com.dtflys.forest.solon.test.address;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestRequest;

@ForestClient
@BaseRequest(baseURL = "http://localhost")
@Address(host = "127.0.0.1", port = "${port}")
public interface SpringBaseAddressClient {

    @Get("/")
    ForestRequest<String> testBaseAddress(@Var("port") int port);

}
