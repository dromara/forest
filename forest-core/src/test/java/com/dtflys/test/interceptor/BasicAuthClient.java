package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.interceptor.extension.BasicAuth;

public interface BasicAuthClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=${username}",
            headers = {"Accept:text/plan"}
    )
    @BasicAuth(username = "${username}", password = "bar")
    String send(@DataVariable("username") String username);

}
