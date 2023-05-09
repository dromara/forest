package org.dromara.test.http.client;

import org.dromara.forest.annotation.BaseURL;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.annotation.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 15:31
 */
@BaseURL("http://localhost:${port}/xxx")
public interface BaseURLClient {

    @Request(
            url = "/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleGet();

    @Get
    String emptyPathGet();

}
