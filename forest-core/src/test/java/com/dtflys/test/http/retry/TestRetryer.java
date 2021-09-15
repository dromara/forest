package com.dtflys.test.http.retry;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.retryer.BackOffRetryer;

public class TestRetryer extends BackOffRetryer {

    public TestRetryer(ForestRequest request) {
        super(request);
    }

}
