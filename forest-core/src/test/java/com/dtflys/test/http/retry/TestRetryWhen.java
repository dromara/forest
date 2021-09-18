package com.dtflys.test.http.retry;

import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public class TestRetryWhen implements RetryWhen {

    @Override
    public boolean retryWhen(ForestRequest request, ForestResponse response) {
        return response.statusIs(203);
    }

}
