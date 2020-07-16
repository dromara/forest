package com.dtflys.test.http.client;

import com.dtflys.forest.annotation.BaseURL;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.annotation.BaseURL;
import com.dtflys.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 15:31
 */
@BaseURL("http://localhost:${port}")
public interface BaseURLClient {

    @Request(
            url = "/hello/user?username=foo",
            headers = {"Accept:text/plan"}
    )
    String simpleGet();


}
