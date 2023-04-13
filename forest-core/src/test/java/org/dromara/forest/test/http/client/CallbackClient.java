package org.dromara.forest.test.http.client;

import org.dromara.forest.annotation.Request;
import org.dromara.forest.annotation.Var;
import org.dromara.forest.callback.OnSuccess;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-06 15:54
 */
public interface CallbackClient {

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${username}"
    )
    String getOnSuccess(@Var("username") String username, OnSuccess<String> onSuccess);


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${username}",
            dataType = "json"
    )
    String getOnSuccessMap(@Var("username") String username, OnSuccess<Map> onSuccess);


}
