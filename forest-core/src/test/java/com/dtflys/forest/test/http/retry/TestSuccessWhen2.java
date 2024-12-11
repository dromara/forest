package com.dtflys.forest.test.http.retry;

import com.dtflys.forest.callback.SuccessWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-16 22:18
 */
public class TestSuccessWhen2 implements SuccessWhen {

    /**
     * 请求成功条件
     * @param req Forest请求对象
     * @param res Forest响应对象
     * @return 是否成功
     */
    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        return res.noException() && res.statusOk();
    }
}
