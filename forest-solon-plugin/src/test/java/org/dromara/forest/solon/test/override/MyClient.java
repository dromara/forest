package org.dromara.forest.solon.test.override;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;

@ForestClient
@BaseRequest(baseURL = "http://localhost:${port}")
public interface MyClient {

    @Get("/test1")
    String test1();


}
