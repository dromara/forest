package org.forest.http.client;

import org.forest.annotation.DataParam;
import org.forest.annotation.DataVariable;
import org.forest.annotation.Request;
import org.forest.callback.OnError;
import org.forest.callback.OnSuccess;

import java.util.Map;
import java.util.concurrent.Future;

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


    @Request(
            url = "http://localhost:5000/hello/user?username=foo",
            async = true,
            headers = {"Accept:text/plan"}
    )
    void asyncSimpleGet(OnSuccess<String> onSuccess);

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
            data = "username=${username}"
    )
    Future<String> asyncVarParamGet(@DataVariable("username") String username, OnSuccess onSuccess, OnError onError);



}
