package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;

/**
 * @title: BaseURLPortClient
 * @Author Zengjie
 * @Date: 2023/9/14 9:59
 * @Version 1.0
 */
@BaseRequest(baseURL = "${baseURL}")
public interface BaseURLPortClient {

    @Get("http://www.baidu.com/a/b/c")
    String getBaidu();

    @Get("http://www.baidu.com:{port}/a/b/c")
    String testPort(@Var("port") String port);
}