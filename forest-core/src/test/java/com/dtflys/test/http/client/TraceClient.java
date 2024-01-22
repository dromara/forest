package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.Query;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.Trace;
import com.dtflys.forest.annotation.TraceRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-07-13 17:37
 */
public interface TraceClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            type = "trace",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleTrace();

    @Trace(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleTrace2();

    @TraceRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleTrace3();

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "trace",
            headers = {"Accept:text/plain"},
            data = "username=${0}"
    )
    String textParamTrace(String username);


    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "trace",
            headers = {"Accept:text/plain"}
    )
    String annParamTrace(@Query("username") String username);


}
