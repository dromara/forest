package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.model.*;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.http.model.XmlTestParam;
import com.dtflys.test.interceptor.PostHeadInterceptor;

import java.util.List;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
@BaseRequest(baseURL = "http://localhost:${port}", contentType = "application/json")
public interface PostClient {


    @Request(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String postHello();

    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "username=foo&password=123456"
    )
    String simplePost(@Header("Accept") String accept);

    @Post(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePost2();

    @PostRequest(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePost3();

    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "username=${0}&password=${1}",
            headers = {"Accept:text/plain"}
    )
    String textParamPost(String username, String password);


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String annParamPost(@DataParam("username") String username, @DataParam("password") String password);


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "username=${username}&password=${password}",
            headers = {"Accept:text/plain"}
    )
    String varParamPost(@DataVariable("username") String username, @DataVariable("password") String password);


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "${user.argString}",
            headers = {"Accept:text/plain"}
    )
    String modelParamPost(@DataVariable("user") UserParam userParam);


    @Request(
            url = "http://localhost:${port}/complex?param=${0}",
            type = "post",
            data = "${1}",
            headers = {"Accept:text/plain"}
    )
    String complexPost(String param, String body);


    @Request(
            url = "http://localhost:${port}/complex?param=${0}",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost2(String param, @DataParam("username") String username, @DataParam("password") String password);

    @Request(
            url = "http://localhost:${port}/complex",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost3(@Query("param") String param,
                        @Body("username") String username,
                        @Body("password") String password);

    @Request(
            url = "http://localhost:${port}/complex",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost4(@Query("param") String param,
                        @DataParam("username") String username,
                        @DataParam("password") String password);

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "post",
            data = "${0}",
            headers = {
                "Accept:text/plain",
                "accessToken:11111111",
            }
    )
    String postHead(String body);

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "post",
            data = "${0}",
            headers = { "Accept:text/plain"},
            interceptor = PostHeadInterceptor.class
    )
    String postHead2(String body);


    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            data = "{\"username\":\"${0}\",\"password\":\"${1}\",\"cn_name\":\"${2}\"}",
            contentType = "application/json; charset=utf-8"
    )
    String postJson(String username, String password, String cnName);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            data = "{\"username\":\"${0}\",\"password\":\"${1}\",\"cn_name\":\"${2}\"}",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson2(String username, String password, String cnName);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            contentType = "application/json; charset=utf-8"
    )
    String postJson3(@DataParam("username") String username, @DataParam("password") String password, @DataParam("cn_name") String cnName);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson4(@DataParam("username") String username, @DataParam("password") String password, @DataParam("cn_name") String cnName);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObj1(@Body JsonTestUser3 user);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObj2(@DataObject JsonTestUser3 user);

    @Post(
            url = "http://localhost:${port}/json",
            data = "{\"username\":\"${user.username}\",\"password\":\"${user.password}\",\"cn_name\":\"${user.cnName}\"}",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObj3(@DataVariable("user") JsonTestUser3 user);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postBodyString1(@Body String body);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postBodyString2(@Body("") String body);

    @Request(
            url = "/json",
            type = "post",
            headers = {
                    "Accept:application/json",
                    "Authorization: ${token}",
            },
            contentType = "application/json"
    )
    ForestResponse<String> postJson5(@DataParam("username") String username, @DataVariable("token") String token);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            contentType = "application/json"
    )
    String postJson5Map(@DataObject Map user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJson6(@DataObject JsonTestUser user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson7(@DataObject List<JsonTestList> user);

    @Request(
            url = "http://localhost:${port}/json",
            data = "${json($0)}",
            type = "post",
            contentType = "application/json; charset=utf-8"
    )
    String postJson8(List<JsonTestList> user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson9(@DataObject JsonTestList user);

    @Request(
            url = "http://localhost:${port}/json",
            data = "${json($0)}",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson10(JsonTestList user);


    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Accept-Encoding: UTF-8"},
            contentType = "application/json"
    )
    String postJson11(@Body JsonTestUser user);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson12(@Body List<JsonTestList> user);

    @Post(
            url = "http://localhost:${port}/json",
            contentType = "application/json"
    )
    String postJson5Map2(@Body Map user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            data = "${json(user)}",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson11(@DataVariable("user") JsonTestUser2 user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJson12(@DataObject JsonTestUser2 user);


    @Request(
            url = "http://localhost:${port}/xml",
            type = "post",
            contentType = "application/xml"
    )
    String postXml(@DataObject(filter = "xml") XmlTestParam testParam);


    @Request(
            url = "http://localhost:${port}/xml",
            type = "post",
            contentType = "application/xml",
            data = "${xml(misc)}"
    )
    String postXml2(@DataVariable("misc") XmlTestParam testParam);


    @Request(
            url = "http://localhost:${port}/xml",
            type = "post",
            contentType = "application/xml",
            data = "${xml($0)}"
    )
    String postXml3(XmlTestParam testParam);




}
