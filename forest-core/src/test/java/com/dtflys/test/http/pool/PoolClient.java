package com.dtflys.test.http.pool;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;

@Address(port = "{port}")
public interface PoolClient {

    @Get(url = "/", timeout = 10000)
    String send();
}
