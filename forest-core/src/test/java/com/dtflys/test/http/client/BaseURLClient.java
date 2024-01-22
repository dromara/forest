package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 15:31
 */
//@BaseRequest(baseURL = "http://localhost:${port}")
@Address(basePath = "http://localhost:${port}")
public interface BaseURLClient {

    @Request(
            url = "/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGet();

    @Get
    String emptyPathGet();


    @Get("http://www.baidu.com")
    String baidu();

}
