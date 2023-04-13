package org.dromara.forest.springboot3.test.address;

import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestRequest;

public interface SpringAddressClient {

    @Get("/")
    ForestRequest<String> testHost();

}
