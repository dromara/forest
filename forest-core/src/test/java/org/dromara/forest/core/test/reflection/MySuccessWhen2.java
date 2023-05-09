package org.dromara.forest.core.test.reflection;

import org.dromara.forest.callback.SuccessWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

/**
 * @author caihongming
 * @since 1.5.17
 **/
public class MySuccessWhen2 implements SuccessWhen {
    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        return res.noException() && res.statusOk();
    }
}
