package org.forest.test;

import org.forest.annotation.Request;
import org.forest.callback.OnError;

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
