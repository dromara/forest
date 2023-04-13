package org.dromara.forest.test;

import org.dromara.forest.annotation.Request;
import org.dromara.forest.callback.OnError;
import org.dromara.forest.http.ForestResponse;

/**
 * @author gongjun
 * @since 2016-06-01
 */
public interface ErrorClient {

    @Request(url = "http://this_is_a_error_address")
    String testError();

    @Request(url = "http://this_is_a_error_address")
    String testError(OnError onError);


    @Request(url = "http://localhost:8080", timeout = 1000)
    ForestResponse<String> testErrorResponse();
}
