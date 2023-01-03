package com.dtflys.forest.solon.test.override;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Get;

@ForestClient
@BaseRequest(baseURL = "http://localhost:${port}")
public interface MyClient {

    @Get("/test1")
    String test1();


}
