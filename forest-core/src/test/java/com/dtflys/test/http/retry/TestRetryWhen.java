package com.dtflys.test.http.retry;

import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

public class TestRetryWhen implements RetryWhen {

    /**
     * 请求重试条件
     * @param request Forest请求对象
     * @param response Forest响应对象
     * @return 是否重试
     */
    @Override
    public boolean retryWhen(ForestRequest request, ForestResponse response) {
        return response.statusIs(203);
    }

}
