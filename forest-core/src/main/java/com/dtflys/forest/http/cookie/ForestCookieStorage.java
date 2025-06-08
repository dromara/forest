package com.dtflys.forest.http.cookie;

import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestURL;

/**
 * Forest Cookie 储存接口
 * 
 * @since 1.7.0
 */
public interface ForestCookieStorage {

    /**
     * 通过 URL 加载 Cookie，并将符合条件到的 Cookie 加载到传入 Cookie 集合中
     * 
     * @param url 请求 URL
     * @param cookies Cookie 集合
     * @since 1.7.0
     */
    default void load(ForestURL url, ForestCookies cookies) {
        final ForestCookies loadedCookies = load(url);
        if (loadedCookies != null && loadedCookies.size() > 0) {
            cookies.addAllCookies(loadedCookies.allCookies());
        }
    }

    /**
     * 通过 URL 获取可加载的 Cookie 集合
     * 
     * @param url 请求 URL
     * @return Forest Cookie 集合
     * @since 1.7.0
     */
    ForestCookies load(ForestURL url);

    /**
     * 保存 Cookie
     * 
     * @param cookies Forest Cookie 集合
     * @since 1.7.0
     */
    void save(ForestCookies cookies);

}
