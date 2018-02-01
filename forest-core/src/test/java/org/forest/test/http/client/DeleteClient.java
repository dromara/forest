package org.forest.test.http.client;

import org.forest.annotation.DataParam;
import org.forest.annotation.Request;

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
            url = "http://localhost:5000/xx/user",
            type = "delete",
            headers = {"Accept:text/plan"},
            data = "username=${0}"
    )
    String textParamDelete(String username);


    @Request(
            url = "http://localhost:5000/xx/user",
            type = "delete",
            headers = {"Accept:text/plan"}
    )
    String annParamDelete(@DataParam("username") String username);

}
