package com.dtflys.forest.springboot.test.address;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.http.ForestRequest;

@BaseRequest(baseURL = "http://localhost")
@Address(host = "127.0.0.1", port = "${port}")
public interface SpringBaseAddressClient {

    @Get("/")
    ForestRequest<String> testBaseAddress(@Var("port") int port);

}
