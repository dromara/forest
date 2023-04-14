package org.dromara.forest.test.http.retry;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.retryer.BackOffRetryer;

public class TestRetryer extends BackOffRetryer {

    public TestRetryer(ForestRequest request) {
        super(request);
    }

    @Override
    protected long nextInterval(int currentCount) {
        return 1000;
    }
}
