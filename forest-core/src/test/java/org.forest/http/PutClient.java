package org.forest.http;

import org.forest.annotation.DataParam;
import org.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:11
 */
public interface PutClient {


    @Request(
            url = "http://localhost:5000/hello",
            type = "put",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plan"}
    )
    String simplePut();

    @Request(
            url = "http://localhost:5000/hello",
            type = "put",
            data = "username=${0}&password=${1}",
            headers = {"Accept:text/plan"}
    )
    String textParamPut(String username, String password);

    @Request(
            url = "http://localhost:5000/hello",
            type = "put",
            headers = {"Accept:text/plan"}
    )
    String annParamPut(@DataParam("username") String username, @DataParam("password") String password);



}
