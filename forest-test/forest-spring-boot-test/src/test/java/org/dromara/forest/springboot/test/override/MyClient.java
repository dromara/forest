package org.dromara.forest.springboot.test.override;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Get;

@BaseRequest(baseURL = "http://localhost:${port}")
public interface MyClient {

    @Get("/test1")
    String test1();


}
