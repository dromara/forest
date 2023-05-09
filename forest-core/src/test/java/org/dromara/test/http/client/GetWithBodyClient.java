package org.dromara.test.http.client;

import org.dromara.forest.annotation.Body;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.backend.httpclient.HttpClient;

public interface GetWithBodyClient {

    @Get(
            url = "http://localhost:${port}/hello/user?param=${0}",
            headers = {"Accept:text/plain"}
    )
    @HttpClient
    String getWithBody1(String param, @Body("username") String username, @Body("password") String password);

}
