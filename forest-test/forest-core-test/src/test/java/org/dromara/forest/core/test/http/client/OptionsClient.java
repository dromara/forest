package org.dromara.forest.core.test.http.client;

import org.dromara.forest.annotation.DataParam;
import org.dromara.forest.annotation.Options;
import org.dromara.forest.annotation.OptionsRequest;
import org.dromara.forest.annotation.Request;
import org.dromara.forest.http.ForestResponse;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 18:25
 */
public interface OptionsClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            type = "options",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleOptions();

    @Options(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleOptions2();

    @OptionsRequest(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    ForestResponse simpleOptions3();

    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "options",
            headers = {"Accept:text/plain"},
            data = "username=${0}"
    )
    String textParamOptions(String username);


    @Request(
            url = "http://localhost:${port}/hello/user",
            type = "options",
            headers = {"Accept:text/plain"}
    )
    String annParamOptions(@DataParam("username") String username);


}
