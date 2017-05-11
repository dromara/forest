package org.forest.http;

import org.forest.annotation.DataParam;
import org.forest.annotation.Request;

import java.util.Map;

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

}
