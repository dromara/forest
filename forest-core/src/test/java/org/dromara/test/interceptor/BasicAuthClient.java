package org.dromara.test.interceptor;

import org.dromara.forest.annotation.DataVariable;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.extensions.BasicAuth;

public interface BasicAuthClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=${username}",
            headers = {"Accept:text/plain"}
    )
    @BasicAuth(username = "${username}", password = "bar")
    String send(@DataVariable("username") String username);

}
