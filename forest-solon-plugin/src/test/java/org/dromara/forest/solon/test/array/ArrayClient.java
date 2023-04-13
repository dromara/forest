package org.dromara.forest.solon.test.array;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;

@ForestClient
@Address(port = "{port}")
public interface ArrayClient {

    @Get("/test?arr[0]=#{data.array[0]}&arr[1]=#{data.array[1]}")
    String arrayQueryFromProperties();

}
