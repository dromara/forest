package org.dromara.forest.core.test.interceptor;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.extensions.BasicAuth;

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
