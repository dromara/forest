package org.forest.client;

import org.forest.annotation.Request;
import org.forest.annotation.DataParam;
import org.forest.callback.OnSuccess;

import java.util.Map;

/**
 * @author gongjun
 * @since 2016-05-31
 */
public interface CallbackClient {

    @Request(
        url = "http://dwz.cn/create.php",
        type = "post",
        dataType = "json",
        headers = {
            "charset:UTF-8",
            "Cache-control:no-cache"
        }
    )
    String testOnSuccess(@DataParam("url") String url, OnSuccess<Map> onSuccess);

}
