package org.dromara.forest.core.test.http.gzip;

import org.dromara.forest.annotation.BaseRequest;
import org.dromara.forest.annotation.DecompressGzip;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.http.ForestResponse;

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
