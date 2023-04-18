/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.dromara.forest.http;

import org.dromara.forest.utils.StringUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Forest Cookie 集合
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public class ForestCookies implements Iterable<ForestCookie> {

    private List<ForestCookie> cookies;

    private boolean strict = true;

    public ForestCookies() {
        this.cookies = new LinkedList<>();
    }

    public ForestCookies(List<ForestCookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * 获取Cookie集合大小
     *
     * @return 集合大小
     */
    public int size() {
        return cookies.size();
    }

    /**
     * 是否严格匹配Cookie
     *
     * @return {@code true}: 严格匹配, {@code false}: 不严格
     * @since 1.5.25
     */
    public boolean strict() {
        return strict;
    }

    /**
     * 设置是否严格匹配Cookie
     *
     * @param strict {@code true}: 严格匹配, {@code false}: 不严格
     * @return Cookie集合，{@link ForestCookies}类实例
     * @since 1.5.25
     */
    public ForestCookies strict(boolean strict) {
        this.strict = strict;
        return this;
    }


    /**
     * 解析请求中的Cookie头信息
     *
     * @param content Cookie头信息
     * @return Cookie集合，{@link ForestCookies}对象实例
     * @since 1.5.23
     */
    public static ForestCookies parse(String content) {
        ForestCookies cookies = new ForestCookies();
        if (StringUtil.isBlank(content)) {
            return cookies;
        }
        String[] pairs = content.split(";");
        for (String pair : pairs) {
            String[] nameValue = pair.split("=", 2);
            if (nameValue.length < 2) {
                continue;
            }
            String name = nameValue[0].trim();
            String value = nameValue[1].trim();
            ForestCookie cookie = new ForestCookie(name, value);
            cookies.addCookie(cookie);
        }
        return cookies;
    }

    /**
     * 根据域名获取Cookie列表
     *
     * @param domain 域名
     * @return Cookie列表
     */
    public List<ForestCookie> getCookies(String domain) {
        List<ForestCookie> list = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            if (cookie.matchDomain(domain)) {
                list.add(cookie);
            }
        }
        return list;
    }

    /**
     * 根据域名和路径获取Cookie列表
     *
     * @param domain 域名
     * @param path 路径
     * @return Cookie列表
     */
    public List<ForestCookie> getCookies(String domain, String path) {
        List<ForestCookie> list = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            if (cookie.matchDomain(domain) &&
                cookie.matchPath(path)) {
                list.add(cookie);
            }
        }
        return list;
    }

    /**
     * 根据域名、路径和名称获取Cookie列表
     *
     * @param domain 域名
     * @param path 路径
     * @param name Cookie名称
     * @return Cookie列表
     */
    public List<ForestCookie> getCookies(String domain, String path, String name) {
        List<ForestCookie> list = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            if (cookie.matchDomain(domain) &&
                    cookie.matchPath(path) &&
                    cookie.getName().equals(name)) {
                list.add(cookie);
            }
        }
        return list;
    }

    /**
     * 添加Cookie
     *
     * @param cookie Cookie对象，{@link ForestCookie}类实例
     * @return Cookie集合，{@link ForestCookies}类实例
     */
    public ForestCookies addCookie(ForestCookie cookie) {
        if (cookie != null) {
            this.cookies.add(cookie);
        }
        return this;
    }

    /**
     * 添加Cookie列表
     *
     * @param cookies Cookie列表
     * @return Cookie集合，{@link ForestCookies}类实例
     */
    public ForestCookies addAllCookies(List<ForestCookie> cookies) {
        if (cookies == null) {
            return this;
        }
        for (ForestCookie cookie : cookies) {
            this.addCookie(cookie);
        }
        return this;
    }

    /**
     * 获取Cookie的域名列表
     *
     * @return 域名列表
     */
    public List<String> domains() {
        List<String> results = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            results.add(cookie.getDomain());
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * 获取Cookie在某域名下的路径列表
     *
     * @param domain 域名
     * @return 路径列表
     */
    public List<String> paths(String domain) {
        List<String> results = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            if (cookie.getDomain().equals(domain)) {
                results.add(cookie.getPath());
            }
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * 获取Cookie的名称列表
     *
     * @return 名称列表
     */
    public List<String> names() {
        List<String> results = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            results.add(cookie.getName());
        }
        return Collections.unmodifiableList(results);
    }

    /**
     * 获取该集合下的所有Cookie
     *
     * @return Cookie列表
     */
    public List<ForestCookie> allCookies() {
        return this.cookies;
    }

    /**
     * 获取Cookie集合的迭代器
     *
     * @return 迭代器
     */
    @Override
    public Iterator<ForestCookie> iterator() {
        return this.cookies.iterator();
    }

}
