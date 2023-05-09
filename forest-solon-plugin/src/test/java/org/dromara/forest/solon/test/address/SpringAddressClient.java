package org.dromara.forest.solon.test.address;

import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestRequest;

@ForestClient
public interface SpringAddressClient {

    @Get("/")
    ForestRequest<String> testHost();

}
