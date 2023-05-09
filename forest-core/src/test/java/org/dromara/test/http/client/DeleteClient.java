package org.dromara.test.http.client;

import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.annotation.Delete;
import org.dromara.forest.annotation.DeleteRequest;
import org.dromara.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:22
 */
public interface DeleteClient {

    @Request(
            url = "http://localhost:${port}/xx/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String deleteUser();

    @Request(
            url = "http://localhost:${port}/xx/user?username=foo",
            type = "delete",
            headers = {"Accept:text/plain"}
    )
    String simpleDelete();

    @Delete(
            url = "http://localhost:${port}/xx/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleDelete2();

    @DeleteRequest(
            url = "http://localhost:${port}/xx/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleDelete3();

    @Request(
            url = "http://localhost:${port}/xx/user",
            type = "delete",
            headers = {"Accept:text/plain"},
            data = "username=${0}"
    )
    String textParamDelete(String username);


    @Request(
            url = "http://localhost:${port}/xx/user",
            type = "delete",
            headers = {"Accept:text/plain"}
    )
    String annParamDelete(@DataParam("username") String username);

}
