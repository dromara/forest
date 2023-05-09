package org.dromara.forest.solon.test.address;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestRequest;

@ForestClient
@BaseRequest(baseURL = "http://localhost")
public interface SpringNoAddressClient {

    @Get("/")
    ForestRequest<String> testHost();

}
