package com.dtflys.forest.test.http.client;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;

/**
 * @title: BaseReqVarClient
 * @Author Microsiland
 * @Date: 2023/9/13 15:33
 * @Version 1.0
 */
@Address(
        basePath = "${baseURL}"
)
public interface BaseReqAddressClient {

    @Get("hello")
    String simpleGetWithoutSlash();

}