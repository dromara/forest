package org.dromara.forest.test.http.retry;

import org.dromara.forest.callback.RetryWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

public class TestRetryWhen2 implements RetryWhen {

    @Override
    public boolean retryWhen(ForestRequest request, ForestResponse response) {
        return false;
    }
}
