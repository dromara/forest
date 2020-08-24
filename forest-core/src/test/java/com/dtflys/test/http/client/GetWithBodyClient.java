package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Get;

public interface GetWithBodyClient {

    @Get(
            url = "http://localhost:${port}/hello/user?param=${0}",
            headers = {"Accept:text/plain"}
    )
    String getWithBody1(String param, @Body("username") String username, @Body("password") String password);

}
