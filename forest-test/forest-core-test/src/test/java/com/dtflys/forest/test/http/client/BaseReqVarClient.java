package com.dtflys.forest.test.http.client;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;

/**
 * @title: BaseReqVarClient
 * @Author Microsiland
 * @Date: 2023/9/13 15:33
 * @Version 1.0
 */
@BaseRequest(
        baseURL = "${baseURL}"
)
public interface BaseReqVarClient {

    @Get("hello")
    String simpleGetWithoutSlash();

    @Get("c/hello")
    String simpleGetWithoutSlash2();

}