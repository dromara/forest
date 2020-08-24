package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:22
 */
public interface DeleteClient {

    @Request(
            url = "http://localhost:4999/xx/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String deleteUser();

    @Request(
            url = "http://localhost:4999/xx/user?username=foo",
            type = "delete",
            headers = {"Accept:text/plain"}
    )
    String simpleDelete();

    @Delete(
            url = "http://localhost:4999/xx/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleDelete2();

    @DeleteRequest(
            url = "http://localhost:4999/xx/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleDelete3();

    @Request(
            url = "http://localhost:4999/xx/user",
            type = "delete",
            headers = {"Accept:text/plain"},
            data = "username=${0}"
    )
    String textParamDelete(String username);


    @Request(
            url = "http://localhost:4999/xx/user",
            type = "delete",
            headers = {"Accept:text/plain"}
    )
    String annParamDelete(@DataParam("username") String username);

}
