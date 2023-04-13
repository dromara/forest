package org.dromara.forest.test.http.client;

import org.dromara.forest.annotation.Request;

public interface RetryClient {

    @Request(
            url = "http://localhost:${port}/hello/user?username=foo",
            headers = {"Accept:text/plain"}
    )
    String simpleRetry();
}
