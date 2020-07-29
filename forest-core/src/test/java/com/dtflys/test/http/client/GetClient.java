package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.model.JsonTestUser;
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
            headers = {"Accept:text/plan"}
    )
    String simpleGet();


    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plan"}
    )
    String errorGet(OnError onError);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plan"}
    )
    ForestResponse<String> errorGet2();


    @Request(
            url = "http://localhost:${port}/hello/user",
            dataType = "json",
            headers = {"Accept:text/plan"},
            data = "username=foo"
    )
    Map jsonMapGet();


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plan"},
            data = "username=${$0.toString()}"
    )
    String textParamGet(String username);


    @Request(
            url = "http://localhost:${port}/hello/user?username=${0}",
            headers = {"Accept:text/plan"}
    )
    String textParamInPathGet(String username);


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plan"}
    )
    String annParamGet(@DataParam("username") String username);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plan"}
    )
    String annObjectGet(@DataObject JsonTestUser user);


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plan"},
            data = "username=${username}"
    )
    String varParamGet(@DataVariable("username") String username);


    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plan"}
    )
    void asyncSimpleGet(OnSuccess<String> onSuccess);

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            dataType = "json",
            async = true,
            headers = {"Accept:text/plan"}
    )
    void asyncSimpleGet2(OnSuccess<TestResult> onSuccess);

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            dataType = "json",
            async = true,
            headers = {"Accept:text/plan"}
    )
    void asyncSimpleGet3(OnSuccess<TestResult<JsonTestUser>> onSuccess);

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plan"}
    )
    Future<String> asyncSimpleGetWithFuture();


    @Request(
            url = "http://localhost:5000/hello/user",
            async = true,
            headers = {"Accept:text/plan"},
            data = "username=${ username.toString() }"
    )
    Future<String> asyncVarParamGet(@DataVariable("username") String username, OnSuccess onSuccess, OnError onError);



}
