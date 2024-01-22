package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 18:25
 */
public interface OptionsClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            type = "options",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleOptions();

    @Options(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleOptions2();

    @OptionsRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleOptions3();

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "options",
            headers = {"Accept:text/plain"},
            data = "username=${0}"
    )
    String textParamOptions(String username);


    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "options",
            headers = {"Accept:text/plain"}
    )
    String annParamOptions(@Query("username") String username);


}
