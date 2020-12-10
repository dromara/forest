package com.dtflys.forest.http;

import okhttp3.Cookie;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Forest Cookie Map集合
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.0-RC1
 */
public class ForestCookieMap {

    private List<ForestCookie> cookies;

    public ForestCookieMap() {
        this.cookies = new LinkedList<>();
    }

    public int size() {
        return cookies.size();
    }


    public List<ForestCookie> getCookies(String domain) {
        List<ForestCookie> list = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            if (cookie.matchDomain(domain)) {
                list.add(cookie);
            }
        }
        return list;
    }

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


    public void addCookie(ForestCookie cookie) {
        this.cookies.add(cookie);
    }

    public void setCookie(ForestCookie cookie) {
        for (ForestCookie item : cookies) {
            if (item.getName().equalsIgnoreCase(cookie.getName())) {

            }
        }
    }

    public List<String> domains() {
        List<String> results = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            results.add(cookie.getDomain());
        }
        return Collections.unmodifiableList(results);
    }

    public List<String> paths(String domain) {
        List<String> results = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            if (cookie.getDomain().equals(domain)) {
                results.add(cookie.getPath());
            }
        }
        return Collections.unmodifiableList(results);
    }

    public List<String> names() {
        List<String> results = new LinkedList<>();
        for (ForestCookie cookie : cookies) {
            results.add(cookie.getName());
        }
        return Collections.unmodifiableList(results);
    }

    public List<ForestCookie> allCookies() {
        return this.cookies;
    }



}
