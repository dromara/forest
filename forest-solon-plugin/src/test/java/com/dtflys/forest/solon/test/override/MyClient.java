package com.dtflys.forest.solon.test.override;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;

@BaseRequest(baseURL = "http://localhost:${port}")
public interface MyClient {

    @Get("/test1")
    String test1();


}
