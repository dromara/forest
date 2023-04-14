package org.dromara.forest.test.http.pool;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.callback.OnError;
import org.dromara.forest.callback.OnSuccess;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

@Address(host = "localhost", port = "{port}")
public interface PoolClient {

    @Get(url = "/", connectTimeout = 10000)
    ForestResponse<String> send();


    @Get(url = "/", connectTimeout = 10000)
    @Address(host = "{0}", port = "{port}")
    ForestRequest send(String host);


    @Get(url = "/{count}", connectTimeout = 10000, async = true)
    ForestResponse<String> sendAsync(@Var("count") int count, OnSuccess<String> onSuccess, OnError onError);
}
