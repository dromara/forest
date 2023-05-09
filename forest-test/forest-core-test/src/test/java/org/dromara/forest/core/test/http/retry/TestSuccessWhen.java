package org.dromara.forest.core.test.http.retry;

import org.dromara.forest.callback.SuccessWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-16 22:18
 */
public class TestSuccessWhen implements SuccessWhen {

    /**
     * 请求成功条件
     * @param req Forest请求对象
     * @param res Forest响应对象
     * @return 是否成功
     */
    @Override
    public boolean successWhen(ForestRequest req, ForestResponse res) {
        return res.noException() && res.statusOk() && res.statusCode() != 203;
    }
}
