package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;

/**
 * 回调函数: 在触发请求重试时执行
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.2
 */
public interface OnRetry {

    /**
     * 回调函数: 在触发请求重试时执行
     *
     * @param request Forest请求对象
     * @param response Forest响应对象
     */
    void onRetry(ForestRequest request, ForestResponse response);

}
