package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.http.ForestFuture;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.Lazy;
import com.dtflys.test.http.model.JsonTestUser;
import com.dtflys.test.interceptor.AddQueryInterceptor;
import com.dtflys.test.interceptor.ErrorInterceptor;
import com.dtflys.test.model.TestResult;
import com.dtflys.test.model.TokenResult;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:24
 */
public interface GetClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain", "content-type: text/plain"}
    )
    String simpleGet();

    @Get(
            url = "https://gitee.com/notifications/unread_count"
    )
    String simpleGet2();

    @GetRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGet3();

    @Get(
            url = "http://localhost:${port}/${path}",
            headers = {"Accept:text/plain"}
    )
    @LogEnabled(false)
    String testPath(@Var("path") String path);

    @Get(
            url = "http://localhost:${port}/{path}",
            headers = {"Accept:text/plain"}
    )
    String testPath2(@Var("path") String path);

    @Get("https://localhost/xxx:yyy")
    ForestRequest testPath3();

    @Get("https://localhost/xxx:111")
    ForestRequest testPath4();

    @Get("http://aaa/bbb/skip:123456@localhost:{port}")
    ForestRequest testPath_userInfo();

    @Request(
            url = "http://localhost:{port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGetMultiQuery(@Query("password") String password);

    @Get(url = "http://localhost:${port}/boolean/true")
    Boolean getBooleanResultTrue();

    @Get(url = "http://localhost:${port}/boolean/false")
    Boolean getBooleanResultFalse();


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String simpleGetMultiQuery2(@Query("username") String username, @Query("password") String password);

    @Request(
            url = "http://localhost:${port}/hello/user?username=${username}&password=${password}",
            headers = {"Accept:text/plain"}
    )
    String simpleGetMultiQuery2WithVar(@Var("username") String username, @Var("password") String password);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
String simpleGetMultiQuery2WithLazy(@Query("a") String a, @Query("b") String b, @Query("token") Lazy<String> password);

    @Request(
            url = "http://localhost:${port}/hello",
            headers = {"Accept:text/plain"},
            interceptor = AddQueryInterceptor.class
    )
    String simpleGetMultiQuery2();

    @Request(
            url = "http://localhost:${port}/hello?username=foo",
            headers = {"Accept:text/plain"},
            interceptor = AddQueryInterceptor.class
    )
    ForestResponse<String> simpleGetMultiQuery4();


    @Request(
            url = "http://localhost:${port}/hello",
            headers = {"Accept:text/plain"},
            interceptor = AddQueryInterceptor.class
    )
    ForestResponse<String> simpleGetMultiQuery5(@Query("username") String username);


    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    @LogEnabled(logResponseContent = true)
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
            maxRetryInterval = 2000,
            timeout = 5,
            headers = {"Accept:text/plain"}
    )
    String errorGetWithRetry(OnError onError);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            retryCount = 5,
            maxRetryInterval = 2000,
            timeout = 1,
            headers = {"Accept:text/plain"}
    )
    String errorGetWithRetry();


    @Request(
            url = "http://localhost:${port}/hello/user",
            dataType = "json",
            headers = {"Accept:text/plain"},
            data = "username=foo"
    )
    ForestResponse<Map> jsonMapGet();

    @Get(
            url = "http://localhost:${port}/hello/user",
            dataType = "json",
            headers = {"Accept:text/plain"},
            responseEncoding = "GBK",
            data = "username=foo"
    )
    ForestResponse<Map> jsonMapGetWithResponseEncoding();

    @Request(
            url = "http://localhost:${port}/hello/user",
            dataType = "json",
            headers = {"Accept:text/plain"},
            data = "username=foo"
    )
    JsonNode jsonMapGet2();


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${$0.toString()}"
    )
    String textParamGet(String username);

    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String textParamGetWithDefaultUsername(@Query(name = "username", defaultValue = "foo") String username);


    @Request(
            url = "http://localhost:${port}/hello/user?username={0}",
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

    @Get(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String queryObjectGet(@Query Map<String, Object> user);

    @Get(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String queryObjectGet(@Query JsonTestUser user);

    @Get(
            url = "http://localhost:${port}/hello/user?username=foo&username=bar&username=user1&username=user2&password=123456",
            headers = {"Accept:text/plain"}
    )
    String repeatableQuery();

    @Get(
            url = "http://localhost:${port}/hello/user?username=foo&username=bar",
            headers = {"Accept:text/plain"}
    )
    String repeatableQuery(@Query("username") String user1, @Query("username") String user2, @Query("password") String password);

    @Get(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String repeatableQuery(@Query("username") String[] usernames, @Query("password") String password);

    @Get(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String repeatableQuery(@Query("username") List<String> usernames, @Query("password") String password);



    @Get(
            url = "http://localhost:${port}/hello/user/array",
            headers = {"Accept:text/plain"}
    )
    String arrayQuery(@Query(name = "username_${_index}") List<String> usernames, @Query("password") String password);



    @Get(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String jsonQuery(@JSONQuery("ids") List<Integer> idList, @JSONQuery("user") Map<String, String> userInfo);


    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String annObjectGet(@Query Map<String, Object> user);



    @Request(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"},
            data = "username={username}"
    )
    String varParamGet(@DataVariable("username") String username);


    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGet(OnSuccess onSuccess);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            async = true,
            maxRetryInterval = 500,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGetError(OnError onError);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            async = true,
            retryCount = 2,
            maxRetryInterval = 500,
            headers = {"Accept:text/plain"}
    )
    void retrySimpleGetError(OnError onError);


    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            timeout = 1,
            async = true,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGetTimeout(OnError onError);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            timeout = 1,
            async = true,
            retryCount = 2,
            maxRetryInterval = 500,
            headers = {"Accept:text/plain"}
    )
    void retrySimpleGetTimeout(OnError onError);



    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGet2(OnSuccess onSuccess);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    void asyncSimpleGet3(OnSuccess onSuccess);

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    Future<String> asyncSimpleGetWithFuture();


    @Request(
            url = "http://localhost:${port}/hello/user",
            async = true,
            headers = {"Accept:text/plain"},
            timeout = 3000,
            data = "username={ username.toString() }"
    )
    Future<String> asyncVarParamGet(@DataVariable("username") String username, OnSuccess onSuccess, OnError onError);


    @Get(
            url = "http://localhost:${port}/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plain"}
    )
    ForestFuture asyncSimpleGetWithForestFuture();


    @Get(url = "http://localhost:${port}?token=YmZlNDYzYmVkMWZjYzgwNjExZDVhMWM1ODZmMWRhYzg0NTcyMGEwMg==")
    ForestResponse<String> testUrl();

    @Get(url = "${schema}://${host}:${port}/${path}?token=YmZlNDYzYmVkMWZjYzgwNjExZDVhMWM1ODZmMWRhYzg0NTcyMGEwMg==")
    ForestResponse<String> testUrl2(@Var("schema") String schema, @Var("host") String host, @Var("path") String path);

    @Get(url = "${url}")
    ForestResponse<String> testUrl3(@Var("url") String url);

    @Get(url = "${url}")
    ForestRequest<String> testUrl4(@Var("url") String url);

    @Get(url = "http://localhost:${port}/test#${ref}")
    ForestResponse<String> testRef(@Var("ref") String ref);

    @Get(url = "http://localhost:${port}/test{ref}")
    ForestResponse<String> testRef2(@Var("ref") String ref);

    @Get(url = "http://localhost:${port}/test{ref}{ref2}")
    ForestResponse<String> testRef3(@Var("ref") String ref, @Var("ref2") String ref2);

    @Get(url = "https://www.${domain}.com/xxx")
    ForestRequest<String> testDomain(@Var("domain") String domain);

    @Get(url = "https://{domain}/xxx")
    ForestRequest<String> testDomain2(@Var("domain") String domain);

    @Get(url = "https://{domain}.dtflyx.com/xxx")
    ForestRequest<String> testDomain3(@Var("domain") String domain);

    @Get(
            url = "http://localhost:${port}/hello/user?{name}",
            headers = {"Accept:text/plain"}
    )
    String getQueryStringWithoutName(@DataVariable("name") String name);

    @Get(
            url = "http://localhost:${port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    String getQueryStringWithoutName2(@Query String name);

    @Get(
            url = "http://xxxxxx:yyyy@localhost:{port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    ForestResponse<String> getUrlWithUserInfo();

    @Get(
            url = "http://xxxxxx:1234@localhost:{port}/hello/user",
            headers = {"Accept:text/plain"}
    )
    ForestResponse<String> getUrlWithUserInfo2();

    @Get(
            url = "http://{encode(userInfo)}@localhost:{port}/hello/user?name=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse<String> getUrlWithUserInfo3(@Var(value = "userInfo") String userInfo);



    @Get("http://localhost:{port}/token")
    TokenResult getToken();

}
