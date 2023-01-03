package com.dtflys.forest.solon.test.address;

import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestRequest;

@ForestClient
public interface SpringAddressClient {

    @Get("/")
    ForestRequest<String> testHost();

}
