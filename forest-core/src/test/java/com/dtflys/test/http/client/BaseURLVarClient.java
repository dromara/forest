package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseURL;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.BaseURL;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 17:24
 */
@BaseURL("${baseURL}")
public interface BaseURLVarClient {

    @Request(
            url = "/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGet();

}
