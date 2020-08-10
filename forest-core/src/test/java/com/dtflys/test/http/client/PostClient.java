package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
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
public interface PostClient {


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plan"}
    )
    String simplePost();


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "username=${0}&password=${1}",
            headers = {"Accept:text/plan"}
    )
    String textParamPost(String username, String password);


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            headers = {"Accept:text/plan"}
    )
    String annParamPost(@DataParam("username") String username, @DataParam("password") String password);


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "username=${username}&password=${password}",
            headers = {"Accept:text/plan"}
    )
    String varParamPost(@DataVariable("username") String username, @DataVariable("password") String password);


    @Request(
            url = "http://localhost:${port}/hello",
            type = "post",
            data = "${user.argString}",
            headers = {"Accept:text/plan"}
    )
    String modelParamPost(@DataVariable("user") UserParam userParam);


    @Request(
            url = "http://localhost:${port}/complex?param=${0}",
            type = "post",
            data = "${1}",
            headers = {"Accept:text/plan"}
    )
    String complexPost(String param, String body);


    @Request(
            url = "http://localhost:${port}/complex?param=${0}",
            type = "post",
            headers = {"Accept:text/plan"}
    )
    String complexPost2(String param, @DataParam("username") String username, @DataParam("password") String password);

    @Request(
            url = "http://localhost:${port}/complex",
            type = "post",
            headers = {"Accept:text/plan"}
    )
    String complexPost3(@DataQuery("param") String param,
                        @DataBody("username") String username,
                        @DataBody("password") String password);

    @Request(
            url = "http://localhost:${port}/complex",
            type = "post",
            headers = {"Accept:text/plan"}
    )
    String complexPost4(@DataQuery("param") String param,
                        @DataParam("username") String username,
                        @DataParam("password") String password);

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "post",
            data = "${0}",
            headers = {
                "Accept:text/plan",
                "accessToken:11111111",
            }
    )
    String postHead(String body);

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "post",
            data = "${0}",
            headers = { "Accept:text/plan"},
            interceptor = PostHeadInterceptor.class
    )
    String postHead2(String body);


    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            data = "{\"username\":\"${0}\",\"password\":\"${1}\"}",
            contentType = "application/json"
    )
    String postJson(String username, String password);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            data = "{\"username\":\"${0}\",\"password\":\"${1}\"}",
            headers = {"Content-Type: application/json"}
    )
    String postJson2(String username, String password);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            contentType = "application/json"
    )
    String postJson3(@DataParam("username") String username, @DataParam("password") String password);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJson4(@DataParam("username") String username, @DataParam("password") String password);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            contentType = "application/json"
    )
    String postJson5(@DataObject JsonTestUser user);

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
            headers = {"Content-Type: application/json"}
    )
    String postJson7(@DataObject List<JsonTestList> user);

    @Request(
            url = "http://localhost:${port}/json",
            data = "${json($0)}",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJson8(List<JsonTestList> user);

    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJson9(@DataObject JsonTestList user);

    @Request(
            url = "http://localhost:${port}/json",
            data = "${json($0)}",
            type = "post",
            headers = {"Content-Type: application/json"}
    )
    String postJson10(JsonTestList user);



    @Request(
            url = "http://localhost:${port}/json",
            type = "post",
            data = "${json(user)}",
            headers = {"Content-Type: application/json"}
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
