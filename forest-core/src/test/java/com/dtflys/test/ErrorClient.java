package com.dtflys.test;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;

/**
 * @author gongjun
 * @since 2016-06-01
 */
public interface ErrorClient {

    @Request(url = "http://this_is_a_error_address", timeout = 10)
    String testError();

    @Request(url = "http://this_is_a_error_address", timeout = 10)
    String testError(OnError onError);


}
