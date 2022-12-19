package com.dtflys.forest.solon.test.address;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestRequest;

@ForestClient
@BaseRequest(baseURL = "http://localhost")
public interface SpringNoAddressClient {

    @Get("/")
    ForestRequest<String> testHost();

}
