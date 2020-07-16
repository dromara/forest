package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:11
 */
public interface PatchClient {


    @Request(
            url = "http://localhost:${port}/hello",
            type = "patch",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plan"}
    )
    String simplePatch();

    @Request(
            url = "http://localhost:${port}/hello",
            type = "patch",
            data = "username=${0}&password=${1}",
            headers = {"Accept:text/plan"}
    )
    String textParamPatch(String username, String password);

    @Request(
            url = "http://localhost:${port}/hello",
            type = "patch",
            headers = {"Accept:text/plan"}
    )
    String annParamPatch(@DataParam("username") String username, @DataParam("password") String password);



}
