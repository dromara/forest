package org.dromara.forest.springboot3.test.array;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Get;

@Address(port = "{port}")
public interface ArrayClient {

    @Get("/com/dtflys/forest/springboot3/test")
    String arrayQueryFromProperties();

}
