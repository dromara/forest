package com.dtflys.test.encode;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-04-13
 **/
@BaseRequest(
        baseURL = "${baseUrl}:${port}/",
        headers = {"Accept: */*"
                , "Content-Type: application/json; charset=UTF-8"
                , "Accept-Language: zh-CN,zh;q=0.8", "Accept-Encoding: gzip, deflate"
                , "Cache-Control: no-cache"}
)
public interface DemoClient {

    /**
     * 调用接口的公共请求
     *
     * @param infno
     * @return
     */
    @Get("/${0}")
    ForestResponse<String> transaction(String infno);
}
