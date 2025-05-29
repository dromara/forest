package com.dtflys.forest.test.http.response;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestResponse;

@Address(host = "localhost", port = "{port}")
public interface ResponseClient {

    @Get("/headers")
    ForestResponse<String> getResponseHeaders();

}
