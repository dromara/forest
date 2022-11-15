package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.http.ForestRequest;
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
@BaseRequest(baseURL = "http://localhost:{port}", contentType = "application/json")
public interface PostClient {

    @Post(
            url = "http://localhost:{port}/hello",
            headers = {"Accept:text/plain"}
    )
    String emptyBody();

    @Request(
            url = "http://localhost:{port}/hello",
            data = "username=foo&password=123456",
            headers = "Accept: text/html"
    )
    @Headers({
            "Accept: text/plain",
            "Content-Type: application/x-www-form-urlencoded"
    })
    String postHello();

    @Headers({
            "Accept: text/plain",
            "Content-Type: application/x-www-form-urlencoded"
    })
    @Request(
            url = "http://localhost:{port}/hello",
            type = "post",
            data = "username=foo&password=123456"
    )
    String simplePost(@Header("Accept") String accept);

    @Request(
            url = "http://localhost:{port}/hello",
            type = "post",
            data = "username=foo&password=123456"
    )
    @HTTPProxy(
            host = "127.0.0.1",
            port = "{0}",
            username = "foo",
            password = "{2}"
    )
    String simplePostWithProxy(int proxyPort, @Header("Accept") String accept, String authorization);

    @Post(
            url = "http://localhost:{port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePost2();

    @PostRequest(
            url = "http://localhost:{port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePost3();

    @Post
    String emptyPath();

    @Request(
            url = "http://localhost:{port}/hello",
            type = "post",
            data = "username={0}&password={1}",
            headers = {"Accept:text/plain"}
    )
    String textParamPost(String username, String password);


    @Request(
            url = "http://localhost:{port}/hello",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String annParamPost(@DataParam("username") String username, @DataParam("password") String password);


    @Request(
            url = "http://localhost:{port}/hello",
            type = "post",
            data = "username={username}&password={password}",
            headers = {"Accept:text/plain"}
    )
    String varParamPost(@Var("username") String username, @Var("password") String password);

    @Post(
            url = "http://localhost:{port}/hello-list",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String listBodyPost(@Body("item_{_index}") List<String> list);


    @Request(
            url = "http://localhost:{port}/hello",
            type = "post",
            data = "{user.argString}",
            headers = {"Accept:text/plain"}
    )
    String modelParamPost(@Var("user") UserParam userParam);


    @Request(
            url = "http://localhost:{port}/complex?param={0}",
            type = "post",
            data = "{1}",
            headers = {"Accept:text/plain"}
    )
    String complexPost(String param, String body);


    @Request(
            url = "http://localhost:{port}/complex?param={0}",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost2(String param, @DataParam("username") String username, @DataParam("password") String password);

    @Request(
            url = "http://localhost:{port}/complex",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost3(@Query("param") String param,
                        @Body("username") String username,
                        @Body("password") String password);

    @Post(
            url = "http://localhost:{port}/complex",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost3Map(@Query("param") String param, @Body Map map);



    @Request(
            url = "http://localhost:{port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormListWithBodyAnn(@Body("username") String username,
                                   @Body("password") String password,
                                   @Body("idList") List<Integer> idList,
                                   @Body("cause") List<Cause> causes);


    @Request(
            url = "http://localhost:{port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormListWithBodyAnn2(@Body FormListParam param);


    @Request(
            url = "http://localhost:{port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormListWithBodyAnn3(@Body("username") String username,
                                    @Body("password") String password,
                                    @Body("idList") Integer[] idList,
                                    @Body("cause") Cause[] causes);

    @Request(
            url = "http://localhost:{port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormListWithBodyAnn4(@Body FormArrayParam param);

    @Request(
            url = "http://localhost:{port}/form-array",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String postFormListWithBodyAnn_jsonParam(@Body(value = "param") String param);

    @Request(
            url = "http://localhost:{port}/complex",
            type = "post",
            headers = {"Accept:text/plain"},
            contentType = "application/x-www-form-urlencoded"
    )
    String complexPost4(@Query("param") String param,
                        @DataParam("username") String username,
                        @DataParam("password") String password);

    @Request(
            url = "http://localhost:{port}/hello/user",
            type = "post",
            data = "{0}",
            headers = {
                "Accept:text/plain",
                "accessToken:11111111",
            }
    )
    String postHead(String body);

    @Request(
            url = "http://localhost:{port}/hello/user",
            type = "post",
            data = "{0}",
            headers = { "Accept:text/plain" },
            interceptor = PostHeadInterceptor.class
    )
    String postHead2(String body);

    @Post(
            url = "http://localhost:{port}/hello/user",
            contentType = ContentType.APPLICATION_JSON
    )
    String postJsonByteArray(@Body byte[] data);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            data = "{\"username\":\"{0}\",\"password\":\"{1}\",\"cn_name\":\"{2}\"}",
            contentType = "application/json; charset=utf-8"
    )
    @BodyType("binary")
    String postJsonWithCnCharacters(String username, String password, String cnName);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            data = "{\"username\":\"{0}\",\"password\":\"{1}\",\"cn_name\":\"{2}\"}",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonWithCnCharacters2(String username, String password, String cnName);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            contentType = "application/json; charset=utf-8"
    )
    String postJsonWithCnCharacters3(@DataParam("username") String username, @DataParam("password") String password, @DataParam("cn_name") String cnName);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonWithCnCharacters4(@DataParam("username") String username, @DataParam("password") String password, @DataParam("cn_name") String cnName);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonWithCnCharacters5(@Body JsonTestUser3 user);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonWithCnCharacters6(@DataObject JsonTestUser3 user);

    @Post(
            url = "http://localhost:{port}/json",
            data = "{\"username\":\"{user.username}\",\"password\":\"{user.password}\",\"cn_name\":\"{user.cnName}\"}",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonWithCnCharacters7(@Var("user") JsonTestUser3 user);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonWithNullField(@Body JsonTestUser3 user);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonWithNullField_NotNull(@Body JsonTestUser4 user);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postBodyCnStringWithBodyAnn(@Body String body);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postBodyCnStringWithBodyAnnAndEmptyName(@Body("") String body);


    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postBodyCnStringWithDefaultBody(
            @Body(defaultValue = "{\"username\":\"foo\",\"password\":\"123456&&++===\",\"cn_name\":\"中文名\"}") String body);

    @Request(
            url = "/json",
            type = "post",
            headers = {
                    "Accept:application/json",
                    "Authorization: {token}",
            },
            contentType = "application/json"
    )
    @LogEnabled
    ForestResponse<String> postJsonWithLog(@DataParam("username") String username, @DataVariable("token") String token);


    @Post("http://localhost:{port}/json")
    String postJsonBodyMap(@JSONBody Map user);

    @Post("http://localhost:{port}/json")
    ForestRequest<String> postJsonBodyList(@JSONBody List<Map> users);

    @Post("http://localhost:{port}/json")
    ForestRequest<String> postJsonBodyArray(@JSONBody Map[] users);

    @Post("http://localhost:{port}/json")
    String postJsonBodyMapWithDefaultBody(
            @JSONBody(defaultValue = "{\"username\":\"foo\"}") Map user);


    @Post(
            url = "http://localhost:{port}/json",
            contentType = "application/json"
    )
    String postJsonBodyMap2(@JSONBody Map user);

    @Post(
            url = "http://localhost:{port}/json",
            contentType = "{1}"
    )
    String postJsonBodyMapError(@JSONBody Map user, String contentType);

    @Post(
            url = "http://localhost:{port}/json",
            contentType = "{1}"
    )
    ForestResponse<String> postJsonBodyMapError2(@JSONBody Map user, String contentType);


    @Post(url = "http://localhost:{port}/json")
    String postJsonBodyObj(@JSONBody JsonTestUser user);

    @Post(url = "http://localhost:{port}/json")
    String postJsonBodyField(@JSONBody("username") String username);

    @Post(url = "http://localhost:{port}/json")
    String postJsonBodyString(@JSONBody String body);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            contentType = "application/json",
            logEnabled = false
    )
    @LogEnabled
    String postJsonMapWithLog(@DataObject Map user);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    @LogEnabled(logRequest = false)
    String postJsonObjectWithoutLog(@DataObject JsonTestUser user);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObjListWithDataObjectAnn(@DataObject List<JsonTestList> user);

    @Request(
            url = "http://localhost:{port}/json",
            data = "{json($0)}",
            type = "post",
            contentType = "application/json; charset=utf-8"
    )
    String postJsonObjListInDataProperty(List<JsonTestList> user);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonListInObjWithDataObjectAnn(@DataObject JsonTestList user);

    @Request(
            url = "http://localhost:{port}/json",
            data = "{json($0)}",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonListInObjInDataProperty(JsonTestList user);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonListObjWithDefaultBody(
            @Body(defaultValue = "{\"userList\":[{\"username\":\"foo\"}]}") JsonTestList user);


    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Accept-Encoding: UTF-8"},
            contentType = "application/json"
    )
    @LogEnabled(logResponseStatus = false, logResponseContent = true)
    String postJsonObjectWithLog_content_noStatus(@Body JsonTestUser user);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    @LogEnabled(logRequest = false, logResponseStatus = false, logResponseContent = true)
    String postJsonObjListWithLog_content_noRequest_noStatus(@Body List<JsonTestList> user);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonMapWithBodyAnn(@Body Map data);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObjFromMultipleBodyAnnParams(@Body("name") String name, @Body("data") List<String> data);

    @Post(
            url = "http://localhost:{port}/json",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    String postJsonObjFromMultipleBodyAnnParams2(
            @Body(name = "name", defaultValue = "test") String name,
            @Body(name = "data", defaultValue = "[\"A\",\"B\",\"C\"]") List<String> data);

    @Post(
            url = "http://localhost:{port}/json",
            contentType = "application/json"
    )
    @LogEnabled(false)
    String postJsonMapWithoutLog(@Body Map user);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            data = "{json(user)}",
            headers = {"Content-Type: application/json; charset=utf-8"}
    )
    @LogEnabled(logRequest = false, logResponseStatus = false, logResponseContent = true)
    String postJsonObjectWithLog_content_noStatus(@DataVariable("user") JsonTestUser2 user);

    @Request(
            url = "http://localhost:{port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJsonObjListWithLog_content_noRequest_noStatus(@DataObject JsonTestUser2 user);

    @Post(
            url = "http://localhost:{port}/json-date",
            headers = {"Content-Type: application/json"}
    )
    String postJsonDate(@Body JsonTestDate jsonTestDate);


    @Request(
            url = "http://localhost:{port}/xml",
            type = "post",
            contentType = "application/xml"
    )
    String postXml(@Body(filter = "xml") XmlTestParam testParam);


    @Request(
            url = "http://localhost:{port}/xml",
            type = "post",
            contentType = "application/xml",
            data = "{xml(misc)}"
    )
    String postXmlInDataProperty(@DataVariable("misc") XmlTestParam testParam);


    @Request(
            url = "http://localhost:{port}/xml",
            type = "post",
            contentType = "application/xml",
            data = "{xml($0)}"
    )
    String postXmlInDataProperty2(XmlTestParam testParam);


    @Post("http://localhost:{port}/xml")
    String postXmlWithXMLBodyAnn(@XMLBody XmlTestParam testParam);


    @Post("http://localhost:{port}/xml")
    String postXmlBodyString(@XMLBody String xml);

    @Post(url = "http://localhost:{port}/xml-response", interceptor = XmlResponseInterceptor.class)
    XmlTestParam postXmlWithXMLBodyAnnAndReturnObj(@XMLBody XmlTestParam testParam);


}
