package com.dtflys.forest.test.reflection;

import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

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