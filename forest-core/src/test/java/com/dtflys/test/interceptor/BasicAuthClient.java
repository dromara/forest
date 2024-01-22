package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.extensions.BasicAuth;

public interface BasicAuthClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=${username}",
            headers = {"Accept:text/plain"}
    )
    @BasicAuth(username = "${username}", password = "bar")
    String send(@Query("username") String username);

}
