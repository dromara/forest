package org.dromara.forest.forestspringboot3example.client;

import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestResponse;

public interface Cn12306 {

    @Get(url = "${idServiceUrl}")
    ForestResponse<String> index();

}
