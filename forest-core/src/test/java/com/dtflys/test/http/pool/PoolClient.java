package com.dtflys.test.http.pool;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.http.ForestResponse;

@Address(host = "localhost", port = "{port}")
public interface PoolClient {

    @Get(url = "/", connectTimeout = 10000)
    ForestResponse<String> send();


    @Get(url = "/", connectTimeout = 10000)
    @Address(host = "{0}", port = "{port}")
    ForestResponse<String> send(String host);

}
