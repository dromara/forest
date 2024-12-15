package com.dtflys.forest.test.http.retry;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.retryer.BackOffRetryer;

public class TestRetryer extends BackOffRetryer {

    public TestRetryer(ForestRequest request) {
        super(request);
    }

    @Override
    protected long nextInterval(int currentCount) {
        return 1000;
    }
}
