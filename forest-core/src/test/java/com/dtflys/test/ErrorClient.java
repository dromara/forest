package com.dtflys.test;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.http.ForestResponse;

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
