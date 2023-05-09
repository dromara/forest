package org.dromara.forest.core.test.http.retry;

import org.dromara.forest.callback.SuccessWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

public class ErrorSuccessWhen implements SuccessWhen {
    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        throw new RuntimeException("test error");
    }
}
