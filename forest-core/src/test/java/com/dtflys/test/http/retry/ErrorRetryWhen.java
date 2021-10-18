package com.dtflys.test.http.retry;

import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public class ErrorRetryWhen implements RetryWhen {
    @Override
    public boolean retryWhen(ForestRequest req, ForestResponse res) {
        throw new RuntimeException("test error");
    }
}
