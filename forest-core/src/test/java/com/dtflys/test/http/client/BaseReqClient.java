package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-04-09 16:27
 */
@BaseRequest(
        baseURL = "http://localhost:${port}/base",
        headers = {
                "Accept-Charset: ${encoding}",
                "Accept: text/plain"
        },
        userAgent = "${userAgent}",
        timeout = 2000
)
public interface BaseReqClient {

    @Request(
            url = ""
    )
    ForestResponse simpleGetWithEmptyPath(@Var("encoding") String encoding);


    @Request(
            url = "/hello/user?username=foo"
    )
    String simpleGet(OnSuccess onSuccess, @Var("encoding") String encoding);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo"
    )
    String simpleGetWithoutBaseUrl(OnSuccess onSuccess, @Var("encoding") String encoding);


}
