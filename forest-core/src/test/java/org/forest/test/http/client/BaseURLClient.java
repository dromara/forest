package org.forest.test.http.client;

import org.forest.annotation.BaseURL;
import org.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 15:31
 */
@BaseURL("http://localhost:5000")
public interface BaseURLClient {

    @Request(
            url = "/hello/user?username=foo",
            headers = {"Accept:text/plan"}
    )
    String simpleGet();


}
