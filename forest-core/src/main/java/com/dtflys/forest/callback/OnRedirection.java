package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * 回调函数: 在请求重定向时触发
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.2
 */
@FunctionalInterface
public interface OnRedirection {

    /**
     * 回调函数:在请求重定向时触发
     *
     * @param redirectReq 进行重定向的新请求
     * @param prevReq 上一个请求
     * @param prevRes 上一个请求的响应
     */
    void onRedirection(ForestRequest<?> redirectReq, ForestRequest<?> prevReq, ForestResponse<?> prevRes);

}
