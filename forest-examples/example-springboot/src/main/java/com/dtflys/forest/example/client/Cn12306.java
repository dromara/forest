package com.dtflys.forest.example.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestResponse;

public interface Cn12306 {

    @Get(url = "${idServiceUrl}")
    ForestResponse<String> index();

}
