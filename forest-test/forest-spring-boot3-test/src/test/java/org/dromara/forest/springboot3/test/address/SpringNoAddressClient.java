package org.dromara.forest.springboot3.test.address;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestRequest;

@BaseRequest(baseURL = "http://localhost")
public interface SpringNoAddressClient {

    @Get("/")
    ForestRequest<String> testHost();

}
