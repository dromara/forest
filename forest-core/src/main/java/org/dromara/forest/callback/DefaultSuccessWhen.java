package org.dromara.forest.callback;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

public class DefaultSuccessWhen implements SuccessWhen {
    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        return res.noException() && res.statusOk();
    }
}
