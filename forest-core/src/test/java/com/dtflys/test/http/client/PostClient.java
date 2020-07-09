package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.DataObject;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.http.model.XmlTestParam;
import com.dtflys.forest.annotation.DataObject;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.DataVariable;
import com.dtflys.forest.annotation.Request;
import com.dtflys.test.http.model.UserParam;
import com.dtflys.test.http.model.XmlTestParam;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public interface PostClient {


    @Request(
            url = "http://localhost:5000/hello",
            type = "post",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plan"}
    )
    String simplePost();


    @Request(
            url = "http://localhost:5000/hello",
            type = "post",
            data = "username=${0}&password=${1}",
            headers = {"Accept:text/plan"}
    )
    String textParamPost(String username, String password);


    @Request(
            url = "http://localhost:5000/hello",
            type = "post",
            headers = {"Accept:text/plan"}
    )
    String annParamPost(@DataParam("username") String username, @DataParam("password") String password);


    @Request(
            url = "http://localhost:5000/hello",
            type = "post",
            data = "username=${username}&password=${password}",
            headers = {"Accept:text/plan"}
    )
    String varParamPost(@DataVariable("username") String username, @DataVariable("password") String password);


    @Request(
            url = "http://localhost:5000/hello",
            type = "post",
            data = "${user.argString}",
            headers = {"Accept:text/plan"}
    )
    String modelParamPost(@DataVariable("user") UserParam userParam);


    @Request(
            url = "http://localhost:5000/complex?param=${0}",
            type = "post",
            data = "${1}",
            headers = {"Accept:text/plan"}
    )
    String complexPost(String param, String body);


    @Request(
            url = "http://localhost:5000/json",
            type = "post",
            data = "{\"username\": \"${0}\", \"password\": \"${1}\"}",
            contentType = "application/json"
    )
    String postJson(String username, String password);

    @Request(
            url = "http://localhost:5000/json",
            type = "post",
            data = "{\"username\": \"${0}\", \"password\": \"${1}\"}",
            headers = {"Content-Type: application/json"}
    )
    String postJson2(String username, String password);

    @Request(
            url = "http://localhost:5000/xml",
            type = "post",
            contentType = "application/xml"
    )
    String postXml(@DataObject(filter = "xml") XmlTestParam testParam);


    @Request(
            url = "http://localhost:5000/xml",
            type = "post",
            contentType = "application/xml",
            data = "${xml(misc)}"
    )
    String postXml2(@DataVariable("misc") XmlTestParam testParam);


    @Request(
            url = "http://localhost:5000/xml",
            type = "post",
            contentType = "application/xml",
            data = "${xml($0)}"
    )
    String postXml3(XmlTestParam testParam);




}
