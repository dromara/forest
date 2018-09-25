package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:22
 */
public interface DeleteClient {

    @Request(
            url = "http://localhost:5000/xx/user?username=foo",
            type = "delete",
            headers = {"Accept:text/plan"}
    )
    String simpleDelete();


    @Request(
            url = "http://localhost:5000/xx/user/data",
            type = "delete",
            headers = {"Accept:text/plan"},
            data = "username=${0}"
    )
    String textParamDelete(String username);


    @Request(
            url = "http://localhost:5000/xx/user/data",
            type = "delete",
            headers = {"Accept:text/plan"}
    )
    String annParamDelete(@DataParam("username") String username);

}
