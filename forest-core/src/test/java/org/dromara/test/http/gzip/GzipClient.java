package org.dromara.test.http.gzip;

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
public interface GzipClient {

    /**
     * 调用接口的公共请求
     *
     * @param infno
     * @return
     */
    @Get("/${0}")
    @DecompressGzip
    ForestResponse<String> transaction(String infno);

    @Get("/${0}")
    @DecompressGzip(false)
    ForestResponse<String> transaction_gzip_false(String infno);

    /**
     * 调用接口的公共请求
     *
     * @param infno
     * @return
     */
    @Get("/${0}")
    ForestResponse<String> transaction_without_annotation(String infno);

}
