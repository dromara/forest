package com.dtflys.forest.springboot.test.override;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DecompressGzip;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Header;
import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.http.ForestResponse;

import java.util.Map;

@BaseRequest(baseURL = "http://localhost:${port}")
public interface MyClient {

    @Get("/test1")
    String test1();


}
