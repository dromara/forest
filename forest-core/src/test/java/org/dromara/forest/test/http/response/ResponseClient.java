package org.dromara.forest.test.http.response;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestResponse;

@Address(host = "localhost", port = "{port}")
public interface ResponseClient {

    @Get("/headers")
    ForestResponse<String> getResponseHeaders();

}
