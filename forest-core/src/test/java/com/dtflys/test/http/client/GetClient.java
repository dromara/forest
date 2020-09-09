package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.interceptor.ErrorInterceptor;
import com.dtflys.test.model.TestResult;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:24
 */
public interface GetClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGet();

    @Get(url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGet2();

    @GetRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGet3();



    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String errorGet(OnError onError);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse<String> errorGet2();


    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    Map errorGet3();

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = ErrorInterceptor.class
    )
    ForestResponse<String> errorGet4();

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            retryCount = 3,
            maxRetryInterval = 50000,
            headers = {"Accept:text/plain"}
    )
    String errorGetWithRetry(OnError onError);


    @Request(
            url = "http://localhost:${port}/hello/user",
            dataType = "json",
            headers = {"Accept:text/plain"},
            data = "username=foo"
    )
    Map jsonMapGet();


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${$0.toString()}"
    )
    String textParamGet(String username);


    @Request(
            url = "http://localhost:${port}/hello/user?username=${0}",
            headers = {
                    "Accept:text/plain",
                    "Content-Type: application/json"
            }
    )
    String textParamInPathGet(String username, OnSuccess onSuccess);


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String annParamGet(@DataParam("username") String username);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String annQueryGet(@Query("username") String username);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String annObjectGet(@DataObject JsonTestUser user);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String queryObjectGet(@Query Map<String, Object> user);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String queryObjectGet(@Query JsonTestUser user);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String annObjectGet(@DataObject Map<String, Object> user);



    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${username}"
    )
    String varParamGet(@DataVariable("username") String username);


    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGet(OnSuccess<String> onSuccess);

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGet2(OnSuccess<TestResult> onSuccess);

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGet3(OnSuccess<TestResult<JsonTestUser>> onSuccess);

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    Future<String> asyncSimpleGetWithFuture();


    @Request(
            url = "http://localhost:5000/hello/user",
            async = true,
            headers = {"Accept:text/plain"},
            timeout = 3000,
            data = "username=${ username.toString() }"
    )
    Future<String> asyncVarParamGet(@DataVariable("username") String username, OnSuccess<String> onSuccess, OnError onError);

    @Get(url = "http://localhost:5000?token=YmZlNDYzYmVkMWZjYzgwNjExZDVhMWM1ODZmMWRhYzg0NTcyMGEwMg==")
    ForestResponse<String> testUrl();


    @Get(
            url = "http://localhost:${port}/hello/user?${name}",
            headers = {"Accept:text/plain"}
    )
    String getWithQueryString(@DataVariable("name") String name);

    @Get(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String getWithQueryString2(@Query String name);

    @Get(
            url = "http://xxxxxx:yyyy@localhost:8080/hello/user",
            headers = {"Accept:text/plain"},
            timeout = 100
    )
    ForestResponse<String> getUrlWithAt();

}
