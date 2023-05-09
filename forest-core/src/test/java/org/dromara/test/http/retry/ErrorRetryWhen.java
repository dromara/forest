package org.dromara.test.http.retry;

import org.dromara.forest.callback.RetryWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

public class ErrorRetryWhen implements RetryWhen {
    @Override
    public boolean retryWhen(ForestRequest req, ForestResponse res) {
        throw new RuntimeException("test error");
    }
}
