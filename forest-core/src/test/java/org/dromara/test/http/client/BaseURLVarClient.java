package org.dromara.test.http.client;

import org.dromara.forest.annotation.BaseURL;
import org.dromara.forest.annotation.Request;

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
