package org.dromara.forest.core.test.http.client;

import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.annotation.Patch;
import org.dromara.forest.annotation.PatchRequest;
import org.dromara.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 17:11
 */
public interface PatchClient {

    @Request(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String patchHello();

    @Request(
            url = "http://localhost:${port}/hello",
            type = "patch",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePatch();

    @Patch(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePatch2();

    @PatchRequest(
            url = "http://localhost:${port}/hello",
            data = "username=foo&password=123456",
            headers = {"Accept:text/plain"}
    )
    String simplePatch3();

    @Request(
            url = "http://localhost:${port}/hello",
            type = "patch",
            data = "username=${0}&password=${1}",
            headers = {"Accept:text/plain"}
    )
    String textParamPatch(String username, String password);

    @Request(
            url = "http://localhost:${port}/hello",
            type = "patch",
            headers = {"Accept:text/plain"}
    )
    String annParamPatch(@DataParam("username") String username, @DataParam("password") String password);



}