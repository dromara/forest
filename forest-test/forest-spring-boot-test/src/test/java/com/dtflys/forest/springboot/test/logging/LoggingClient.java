package com.dtflys.forest.springboot.test.logging;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestResponse;

@BaseRequest(baseURL = "http://{server.hostName}:{server.port}")
public interface LoggingClient {

    @Get("/")
    ForestResponse testLogging();
}
