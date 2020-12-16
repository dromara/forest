package com.dtflys.forest.callback;

import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;

/**
 * 回调函数: 需要保存Cookie时调用
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public interface OnSaveCookie {

    /**
     * 在请求响应成功后，需要保存Cookie时调用该方法
     *
     * @param request Forest请求对象
     * @param cookies Cookie集合，通过响应返回的Cookie都从该集合获取
     */
    void onSaveCookie(ForestRequest request, ForestCookies cookies);
}
