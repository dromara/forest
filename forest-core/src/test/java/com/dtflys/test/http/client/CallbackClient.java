package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.callback.OnSuccess;

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
    String getOnSuccess(@Var("username") String username, OnSuccess onSuccess);


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${username}",
            dataType = "json"
    )
    String getOnSuccessMap(@Var("username") String username, OnSuccess onSuccess);


}
