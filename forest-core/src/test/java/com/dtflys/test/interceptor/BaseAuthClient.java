package com.dtflys.test.interceptor;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.interceptor.extension.BasicAuth;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-04 22:17
 */
@BaseRequest(baseURL = "http://localhost:${port}")
@BasicAuth(username = "${username}", password = "bar")
public interface BaseAuthClient {

    @Request(
            url = "/hello/user?username=${username}",
            headers = {"Accept:text/plan"}
    )
    String send(@DataVariable("username") String username);

    @Request(
            url = "/hello/user?username=foo",
            headers = {"Accept:text/plan"}
    )
    String send2(@DataVariable("username") String username);


}
