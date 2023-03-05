package com.dtflys.forest.springboot.test.array;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;

@Address(port = "{port}")
public interface ArrayClient {

    @Get("/test?arr[0]=#{data.array[0]}&arr[1]=#{data.array[1]}")
    String arrayQueryFromProperties();

}
