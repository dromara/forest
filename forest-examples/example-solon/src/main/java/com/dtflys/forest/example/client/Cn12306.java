package org.dromara.forest.example.client;

import org.dromara.forest.annotation.ForestClient;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestResponse;

@ForestClient
public interface Cn12306 {

    @Get(url = "${idServiceUrl}")
    ForestResponse<String> index();

}
