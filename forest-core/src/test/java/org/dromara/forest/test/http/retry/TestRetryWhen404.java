package org.dromara.forest.test.http.retry;

import org.dromara.forest.callback.RetryWhen;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;


public class TestRetryWhen404 implements RetryWhen {

    private final static Logger logger = LoggerFactory.getLogger(TestRetryWhen404.class);

    private AtomicInteger doRetryWhenCount = new AtomicInteger(0);

    /**
     * 请求重试条件
     * @param request Forest请求对象
     * @param response Forest响应对象
     * @return 是否重试
     */
    @Override
    public boolean retryWhen(ForestRequest request, ForestResponse response) {
        logger.info("do retryWhen: 当前重试次数[" + request.getCurrentRetryCount() + "]");
        doRetryWhenCount.incrementAndGet();
        return response.statusIs(404);
    }

    public AtomicInteger getDoRetryWhenCount() {
        return doRetryWhenCount;
    }
}
