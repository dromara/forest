package org.dromara.forest.core.test.http.timeout;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.backend.okhttp3.OkHttp3;
import org.dromara.forest.http.ForestRequest;

@Address(port = "${port}")
public interface TimeoutClient {

    @Get(url = "/", connectTimeout = 10)
    @OkHttp3
    ForestRequest testConnectTimeout();

    @Get(url = "/", readTimeout = 10)
    ForestRequest testReadTimeout();
}
