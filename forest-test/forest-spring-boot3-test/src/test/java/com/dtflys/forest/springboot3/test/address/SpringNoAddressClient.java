package com.dtflys.forest.springboot3.test.address;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestRequest;

@BaseRequest(baseURL = "http://localhost")
public interface SpringNoAddressClient {

    @Get("/")
    ForestRequest<String> testHost();

}
