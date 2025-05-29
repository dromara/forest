package com.dtflys.forest.http;

import com.dtflys.forest.Forest;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.utils.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Forest 请求中的 Cookie 头
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.23
 */
public class ForestCookieHeader extends SimpleHeader {

    private final Map<String, Map<String, ForestCookie>> cookies = new LinkedHashMap<>();

    private final HasURL hasURL;

    public ForestCookieHeader(HasURL hasURL) {
        super("Cookie", null);
        this.hasURL = hasURL;
    }

    /**
     * 根据 Cookie 名称获取 Cookie 列表
     *
     * @param name Cookie 名称
     * @return {@link ForestCookie}对象实例
     */
    public List<ForestCookie> getCookies(String name) {
        final List<ForestCookie> results = new LinkedList<>();
        for (Map<String, ForestCookie> map : cookies.values()) {
            final ForestCookie  cookie = map.get(name.toLowerCase());
            if (cookie != null) {
                results.add(cookie);
            }
        }
        return results;
    }

    /**
     * 根据 path 获取 Cookie 哈希表
     *
     * @param path 路径
     * @return 哈希表, key: 字符串， value: {@link ForestCookie}对象实例
     */
    public Map<String, ForestCookie> getCookieMap(String path) {
        return cookies.get(path);
    }

    /**
     * 根据 Cookie 名称获取单个 Cookie
     *
     * @param name Cookie 名称
     * @return {@link ForestCookie}对象实例
     */
    public ForestCookie getCookie(String name) {
        for (Map.Entry<String, Map<String, ForestCookie>> entry : cookies.entrySet()) {
            final Map<String, ForestCookie> map = entry.getValue();
            final ForestCookie cookie = map.get(name.toLowerCase());
            if (cookie != null) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 根据 Path 和 Cookie 名称获取单个 Cookie
     *
     * @param path 路径
     * @param name Cookie 名称
     * @return {@link ForestCookie}对象实例
     */
    public ForestCookie getCookie(String path, String name) {
        final Map<String, ForestCookie> map = getCookieMap(path);
        if (map == null) {
            return null;
        }
        final ForestCookie  cookie = map.get(name.toLowerCase());
        if (cookie != null) {
            return cookie;
        }
        return null;
    }

    /**
     * 删除 Cookie
     *
     * @param cookie {@link ForestCookie}对象实例
     * @return {@link ForestCookie}对象实例
     */
    public ForestCookie removeCookie(ForestCookie cookie) {
        final Map<String, ForestCookie> map = getCookieMap(cookie.getPath());
        if (map == null) {
            return null;
        }
        return map.remove(cookie);
    }

    /**
     * 添加Cookie头
     *
     * @param cookie {@link ForestCookie}对象实例
     * @return {@code true}: 添加Cookie成功，{@code false}: 添加Cookie失败
     * @since 1.5.23
     */
    public boolean addCookie(ForestCookie cookie) {
        return addCookie(cookie, true);
    }


    /**
     * 添加Cookie头
     *
     * @param cookie {@link ForestCookie}对象实例
     * @param strict 是否严格匹配（只有匹配域名，以及没过期的 Cookie 才能添加）
     * @return {@code true}: 添加Cookie成功，{@code false}: 添加Cookie失败
     * @since 1.5.25
     */
    public boolean addCookie(ForestCookie cookie, boolean strict) {
        if (cookie == null) {
            return false;
        }
        final ForestURL url = hasURL.url();
        if (strict && !cookie.matchURL(url)) {
            return false;
        }
        if (strict && cookie.isPersistent() && cookie.isExpired(new Date())) {
            return false;
        }
        final String name = cookie.getName();
        if (StringUtils.isBlank(name)) {
            return false;
        }
        final String path = cookie.getPath();
        final Map<String, ForestCookie> map = cookies.computeIfAbsent(path, key -> new LinkedHashMap<>());
        map.put(name.toLowerCase(), cookie);
        return true;
    }


    /**
     * 批量添加Cookie头
     *
     * @param cookies {@link ForestCookie}对象列表
     * @since 1.5.23
     */
    public void addCookies(List<ForestCookie> cookies) {
        for (ForestCookie cookie : cookies) {
            addCookie(cookie);
        }
    }

    /**
     * 获取所有 Cookie 列表
     *
     * @return {@link ForestCookie}对象列表
     */
    public List<ForestCookie> getCookies() {
        final List<ForestCookie> results = new LinkedList<>();
        for (Map<String, ForestCookie> map : cookies.values()) {
            for (ForestCookie cookie : map.values()) {
                results.add(cookie);
            }
        }
        return results;
    }


    @Override
    public String getValue() {
        final List<ForestCookie> list = getCookies();
        final int len = list.size();
        if (len == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final ForestCookie cookie = list.get(i);
            builder.append(cookie.getName())
                    .append("=")
                    .append(URLEncoder.ALL.encode(cookie.getValue(), StandardCharsets.UTF_8));
            if (i < len - 1) {
                builder.append("; ");
            }
        }
        return builder.toString();
    }

}
