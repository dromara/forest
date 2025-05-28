package com.dtflys.forest.http.cookie;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.dtflys.forest.http.*;

import java.util.HashSet;
import java.util.Set;

public class MemoryCookieStorage implements ForestCookieStorage {

    private ConcurrentHashSet<ForestCookie> cookieSet;

    private int maxSize;

    public MemoryCookieStorage(int maxSize) {
        this.maxSize = maxSize;
        this.cookieSet = new ConcurrentHashSet<>(maxSize);
    }

    private void refresh() {
        Set<ForestCookie> toRemove = new HashSet<>();
        for (ForestCookie cookie : cookieSet) {
            if (cookie.isExpired(System.currentTimeMillis())) {
                toRemove.add(cookie);
            }
        }
        cookieSet.removeAll(toRemove);
    }

    @Override
    public ForestCookies load(ForestRequest request) {
        int len = cookieSet.size();
        if (len >= maxSize) {
            refresh();
        }
        ForestCookies cookies = new ForestCookies();
        for (ForestCookie cookie : cookieSet) {
            if (cookie.isExpired(System.currentTimeMillis())) {
                cookieSet.remove(cookie);
                continue;
            }
            if (cookie.matchURL(request.url())) {
                cookies.addCookie(cookie);
            }
        }
        return cookies;
    }



    @Override
    public void save(ForestRequest request, ForestResponse response, ForestCookies cookies) {
        for (ForestCookie cookie : cookies) {
            cookieSet.add(cookie);
            if (cookieSet.size() >= maxSize) {
                refresh();
            }
        }
    }
}
