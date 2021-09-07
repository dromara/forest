package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * 回调函数: 是否触发重试
 *
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.2
 */
public interface RetryWhen {

    /**
     * 回调函数: 是否触发重试
     * <p>
     * 该回调函数每次请求响应后或失败后被调用，其返回值将决定这次请求是否需要重试
     *
     * @param request Forest请求对象
     * @param response Forest响应对象
     * @return {@code true} 触发重试, 否则不触发重试
     */
    boolean retryWhen(ForestRequest request, ForestResponse response);
}
