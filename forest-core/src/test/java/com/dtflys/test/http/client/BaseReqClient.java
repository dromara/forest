package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.BaseURL;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-04-09 16:27
 */
@BaseRequest(
        baseURL = "http://localhost:5000",
        headers = {"Accept:text/plan"},
        timeout = 2000
)
public interface BaseReqClient {

    @Request(
            url = "/hello/user?username=foo"
    )
    String simpleGet();

    @Request(
            url = "http://localhost:5000/hello/user?username=foo"
    )
    String simpleGet2();


}
