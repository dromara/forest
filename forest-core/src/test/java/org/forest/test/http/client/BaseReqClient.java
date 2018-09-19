package org.forest.test.http.client;

import org.forest.annotation.BaseRequest;
import org.forest.annotation.BaseURL;
import org.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-04-09 16:27
 */
@BaseURL("http://localhost:5000")
@BaseRequest(
        headers = {"Accept:text/plan"},
        timeout = 2000
)
public interface BaseReqClient {

    @Request(
            url = "/hello/user?username=foo"
    )
    String simpleGet();

}
