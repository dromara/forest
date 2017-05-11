package org.forest.http;

import org.forest.annotation.DataParam;
import org.forest.annotation.DataVariable;
import org.forest.annotation.Request;

import java.util.Map;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:24
 */
public interface GetClient {

    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            headers = {"Accept:text/plan"}
    )
    String simpleGet();


    @Request(
            url = "http://localhost:5000/hello/user",
            dataType = "json",
            headers = {"Accept:text/plan"},
            data = "username=foo"
    )
    Map jsonMapGet();


    @Request(
            url = "http://localhost:5000/hello/user",
            headers = {"Accept:text/plan"},
            data = "username=${0}"
    )
    String textParamGet(String username);


    @Request(
            url = "http://localhost:5000/hello/user?username=${0}",
            headers = {"Accept:text/plan"}
    )
    String textParamInPathGet(String username);


    @Request(
            url = "http://localhost:5000/hello/user",
            headers = {"Accept:text/plan"}
    )
    String annParamGet(@DataParam("username") String username);


    @Request(
            url = "http://localhost:5000/hello/user",
            headers = {"Accept:text/plan"},
            data = "username=${username}"
    )
    String varParamGet(@DataVariable("username") String username);




}
