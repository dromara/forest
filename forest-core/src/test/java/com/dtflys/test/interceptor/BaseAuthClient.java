package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.extensions.BasicAuth;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-04 22:17
 */
@BaseRequest(baseURL = "http://localhost:${port}")
@BasicAuth(username = "${username}", password = "bar")
public interface BaseAuthClient {

    @Request(
            url = "/hello/user?username=${username}",
            headers = {"Accept:text/plain"}
    )
    String send(@Var("username") String username);

    @Request(
            url = "/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String send2(@Var("username") String username);


}
