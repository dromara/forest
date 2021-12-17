package com.dtflys.test.http.retry;

import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public class ErrorSuccessWhen implements SuccessWhen {
    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        throw new RuntimeException("test error");
    }
}
