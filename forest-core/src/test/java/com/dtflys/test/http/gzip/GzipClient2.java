package com.dtflys.test.http.gzip;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DecompressGzip;
import com.dtflys.forest.annotation.Get;
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
@DecompressGzip
public interface GzipClient2 {

    @Get("/${0}")
    ForestResponse<String> transaction(String infno);


    @Get("/none-gzip")
    @DecompressGzip(false)
    ForestResponse<String> noneGzip();
}
