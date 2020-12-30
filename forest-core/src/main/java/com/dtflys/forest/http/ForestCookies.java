package com.dtflys.forest.http;

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

    public ForestCookies() {
        this.cookies = new LinkedList<>();
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
     */
    public void addCookie(ForestCookie cookie) {
        if (cookie != null) {
            this.cookies.add(cookie);
        }
    }

    /**
     * 添加Cookie列表
     *
     * @param cookies Cookie列表
     */
    public void addAllCookies(List<ForestCookie> cookies) {
        if (cookies == null) {
            return;
        }
        for (ForestCookie cookie : cookies) {
            this.addCookie(cookie);
        }
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
