package com.dtflys.forest.test.http.timeout;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.backend.okhttp3.OkHttp3;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

@Address(port = "${port}")
public interface TimeoutClient {

    @Get(url = "/", connectTimeout = 10)
    @OkHttp3
    ForestRequest testConnectTimeout();

    @Get(url = "/", readTimeout = 10)
    ForestRequest testReadTimeout();
}
