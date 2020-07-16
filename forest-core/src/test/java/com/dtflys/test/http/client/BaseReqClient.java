package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.BaseURL;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.test.mock.GetMockServer;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-04-09 16:27
 */
@BaseRequest(
        baseURL = "http://localhost:${port}",
        headers = {"Accept:text/plan"},
        timeout = 2000
)
public interface BaseReqClient {

    @Request(
            url = "/hello/user?username=foo"
    )
    String simpleGet();

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo"
    )
    String simpleGet2();


}
