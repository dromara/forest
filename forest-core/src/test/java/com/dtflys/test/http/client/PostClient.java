package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.model.*;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.http.model.XmlTestParam;
import com.dtflys.test.interceptor.PostHeadInterceptor;
import com.dtflys.test.interceptor.XmlResponseInterceptor;

import java.util.List;
import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
@BaseRequest(baseURL = "http://localhost:${port}", contentType = "application/json")
public interface PostClient {

    @Post(
            url = "http://localhost:${port}/hello",
            headers = {"Accept:text/plain"}
    )
    String emptyBody();

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

    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "username=foo&password=123456"
    )
    @HTTPProxy(
            host = "127.0.0.1",
            port = "10801",
            username = "foo",
            password = "${1}"
    )
    String simplePostWithProxy(@Header("Accept") String accept, String authorization);

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

    @Post(
            url = "http://localhost:${port}/hello-list",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String listBodyPost(@Body("item_${_index}") List<String> list);


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

    @Post(
            url = "http://localhost:${port}/complex",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost3Map(@Query("param") String param, @Body Map map);



    @Request(
            url = "http://localhost:${port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormList1(@Body("username") String username,
                         @Body("password") String password,
                         @Body("idList") List<Integer> idList,
                         @Body("cause") List<Cause> causes);


    @Request(
            url = "http://localhost:${port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormList2(@Body FormListParam param);


    @Request(
            url = "http://localhost:${port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormArray1(@Body("username") String username,
                          @Body("password") String password,
                          @Body("idList") Integer[] idList,
                          @Body("cause") Cause[] causes);

    @Request(
            url = "http://localhost:${port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormArray2(@Body FormArrayParam param);

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
            url = "http://localhost:{port}/json",
            type = "post",
            data = "{\"username\":\"{0}\",\"password\":\"${1}\",\"cn_name\":\"${2}\"}",
            contentType = "application/json; charset=utf-8"
    )
    String postJson(String username, String password, String cnName);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            data = "{\"username\":\"{0}\",\"password\":\"${1}\",\"cn_name\":\"${2}\"}",
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
    String postBodyString1WithDefaultBody(
            @Body(defaultValue = "{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}") String body);

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
            contentType = "application/json",
            keyStore = "ssss"
    )
    @LogEnabled
    ForestResponse<String> postJson5(@DataParam("username") String username, @DataVariable("token") String token);


    @Post("http://localhost:${port}/json")
    String postJsonBodyMap(@JSONBody Map user);

    @Post("http://localhost:${port}/json")
    String postJsonBodyMapWithDefaultBody(
            @JSONBody(defaultValue = "{\"username\":\"foo\"}") Map user);


    @Post(
            url = "http://localhost:${port}/json",
            contentType = "application/json"
    )
    String postJsonBodyMap2(@JSONBody Map user);

    @Post(
            url = "http://localhost:${port}/json",
            contentType = "${1}"
    )
    String postJsonBodyMapError(@JSONBody Map user, String contentType);

    @Post(url = "http://localhost:${port}/json")
    String postJsonBodyObj(@JSONBody JsonTestUser user);

    @Post(url = "http://localhost:${port}/json")
    String postJsonBodyField(@JSONBody("username") String username);

    @Post(url = "http://localhost:${port}/json")
    String postJsonBodyString(@JSONBody String body);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            contentType = "application/json",
            logEnabled = false
    )
    @LogEnabled
    String postJson5Map(@DataObject Map user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    @LogEnabled(logRequest = false)
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

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJson10WithDefaultBody(
            @Body(defaultValue = "{\"userList\":[{\"username\":\"foo\"}]}") JsonTestList user);


    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Accept-Encoding: UTF-8"},
            contentType = "application/json"
    )
    @LogEnabled(logResponseStatus = false, logResponseContent = true)
    String postJson11(@Body JsonTestUser user);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    @LogEnabled(logRequest = false, logResponseStatus = false, logResponseContent = true)
    String postJson12(@Body List<JsonTestList> user);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObjWithList1(@Body Map data);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObjWithList2(@Body("name") String name, @Body("data") List<String> data);

    @Post(
            url = "http://localhost:${port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObjWithList2AndDefaultBody(
            @Body(name = "name", defaultValue = "test") String name,
            @Body(name = "data", defaultValue = "[\"A\",\"B\",\"C\"]") List<String> data);

    @Post(
            url = "http://localhost:${port}/json",
            contentType = "application/json"
    )
    @LogEnabled(false)
    String postJson5Map2(@Body Map user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            data = "${json(user)}",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    @LogEnabled(logRequest = false, logResponseStatus = false, logResponseContent = true)
    String postJson11(@DataVariable("user") JsonTestUser2 user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJson12(@DataObject JsonTestUser2 user);

    @Post(
            url = "http://localhost:${port}/json-date",
            headers = {"Content-Type: application/json"}
    )
    String postJsonDate(@Body JsonTestDate jsonTestDate);


    @Request(
            url = "http://localhost:${port}/xml",
            type = "post",
            contentType = "application/xml"
    )
    String postXml(@Body(filter = "xml") XmlTestParam testParam);


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


    @Post(url = "http://localhost:${port}/xml")
    String postXmlBody(@XMLBody XmlTestParam testParam);


    @Post(url = "http://localhost:${port}/xml")
    String postXmlBodyString(@XMLBody String xml);

    @Post(url = "http://localhost:${port}/xml-response", interceptor = XmlResponseInterceptor.class)
    XmlTestParam postAndGetXmlResponse(@XMLBody XmlTestParam testParam);


}
