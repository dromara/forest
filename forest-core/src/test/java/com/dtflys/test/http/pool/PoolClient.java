package com.dtflys.test.http.pool;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

@Address(host = "localhost", port = "{port}")
public interface PoolClient {

    @Get(url = "/", connectTimeout = 10000)
    ForestResponse<String> send();


    @Get(url = "/", connectTimeout = 10000)
    @Address(host = "{0}", port = "{port}")
    ForestRequest send(String host);


    @Get(url = "/{count}", connectTimeout = 10000, async = true)
    ForestResponse<String> sendAsync(@Var("count") int count, OnSuccess onSuccess, OnError onError);
}
