package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestRequest;

/**
 * @Author Microsiland
 * @Date: 2023/9/14 9:59
 * @Version 1.0
 */
@BaseURL("${baseURL}")
public interface BaseURLPortClient {

    @Get("http://www.baidu.com")
    ForestRequest baidu();
}