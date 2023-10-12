package com.dtflys.forest.springboot3.test.array;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;

@Address(port = "{port}")
public interface ArrayClient {

    @Get("/com/dtflys/forest/springboot/test")
    String arrayQueryFromProperties();

}
