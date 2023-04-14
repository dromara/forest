package org.dromara.forest.test.http.backend;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.Post;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.backend.httpclient.HttpClient;
import org.dromara.forest.backend.okhttp3.OkHttp3;
import org.dromara.forest.http.ForestRequest;

@HttpClient
@Address(host = "localhost", port = "${port}")
public interface BackendClient2 {

    @Post("/")
    ForestRequest<String> testBaseBackend(@Var("port") int port);

    @OkHttp3
    @Post("/")
    ForestRequest<String> testMethodBackend(@Var("port") int port);

}
