package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;

/**
 * 回调函数: 加载Cookie时调用
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public interface OnLoadCookie {

    /**
     * 在发送请求加载Cookie时调用该方法
     *
     * @param request Forest请求对象
     * @param cookies Cookie集合, 需要通过请求发送的Cookie都添加到该集合
     */
    void onLoadCookie(ForestRequest request, ForestCookies cookies);

}
