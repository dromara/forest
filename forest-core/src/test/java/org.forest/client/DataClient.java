package org.forest.client;

import org.forest.annotation.Request;

import java.util.Map;

/**
 * @author gongjun
 * @since 2016-06-12
 */
public interface DataClient {

    @Request(
        url = "http://dwz.cn/create.php",
        type = "post",
        dataType = "json",
        data = "url=${0}"
    )
    Map testData(String url);

}
