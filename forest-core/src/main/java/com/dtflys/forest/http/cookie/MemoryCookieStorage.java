package com.dtflys.forest.http.cookie;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.dtflys.forest.http.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryCookieStorage implements ForestCookieStorage {
    
    private final Map<String, ConcurrentLinkedDeque<ForestCookie>> cookieMap = new ConcurrentHashMap<>();
    
    private AtomicInteger size = new AtomicInteger(0);
    
    private final int threshold;

    private final int maxSize;

    public MemoryCookieStorage(int maxSize) {
        this.maxSize = maxSize;
        this.threshold = Math.min(Math.max(maxSize / 2, 8), this.maxSize);
    }

    private void refresh() {
        for (ConcurrentLinkedDeque<ForestCookie> cookieSet : cookieMap.values()) {
            final Set<ForestCookie> toRemove = new HashSet<>();
            for (ForestCookie cookie : cookieSet) {
                if (cookie.isExpired(System.currentTimeMillis())) {
                    toRemove.add(cookie);
                }
            }
            if (!toRemove.isEmpty()) {
                cookieSet.removeAll(toRemove);
                synchronized (this) {
                    size.set(size.get() - cookieSet.size());
                }
            }
        }
    }

    @Override
    public ForestCookies load(final ForestURL url) {
        final int len = cookieMap.size();
        if (len >= threshold) {
            refresh();
        }
        final ForestCookies cookies = new ForestCookies();
        if (size.get() >= maxSize) {
            return cookies;
        }
        
        final List<ConcurrentLinkedDeque<ForestCookie>> cookieSets = getCookieSetsByURL(url);
        
        for (final ConcurrentLinkedDeque<ForestCookie> cookieSet : cookieSets) {
            final Set<ForestCookie> toRemove = new HashSet<>();
            for (final ForestCookie cookie : cookieSet) {
                if (cookie.isExpired(System.currentTimeMillis())) {
                    toRemove.add(cookie);
                    continue;
                }
                if (cookie.matchURL(url)) {
                    cookies.addCookie(cookie);
                }
            }
            if (!toRemove.isEmpty()) {
                cookieSet.removeAll(toRemove);
                synchronized (this) {
                    size.set(size.get() - cookieSet.size());
                }
            }
        }
        return cookies;
    }

    
    private List<ConcurrentLinkedDeque<ForestCookie>> getCookieSetsByURL(ForestURL url) {
        List<ConcurrentLinkedDeque<ForestCookie>> result = new ArrayList<>();
        final String domain = url.getHost();
        final String[] fragments = domain.split("\\.");
        if (fragments.length == 1 && "localhost".equals(domain)) {
            final ConcurrentLinkedDeque<ForestCookie> cookieSet = getCookieSetByDomainFragments(fragments, 0);
            if (cookieSet != null) {
                result.add(cookieSet);
            }
        } else {
            for (int i = 0; fragments.length - i > 1; i++) {
                final ConcurrentLinkedDeque<ForestCookie> cookieSet = getCookieSetByDomainFragments(fragments, i);
                if (cookieSet != null) {
                    result.add(cookieSet);
                }
            }
        }
        return result;
    }

    private ConcurrentLinkedDeque<ForestCookie> getCookieSetByDomainFragments(final String[] fragments, final int start) {
        if (fragments == null) {
            return null;
        }
        final int len = fragments.length;
        final StringBuilder builder = new StringBuilder();
        for (int i = start; i < len; i++) {
            builder.append(fragments[i]);
            if (i < len - 1) {
                builder.append(".");
            }
        }
        return cookieMap.get(builder.toString());
    }


    @Override
    public void save(ForestCookies cookies) {
        final long currentTime = System.currentTimeMillis();
        for (final ForestCookie cookie : cookies) {
            if (size.get() >= threshold) {
                refresh();
            }
            if (size.get() >= maxSize) {
                return;
            }
            final String domain = cookie.getDomain();
            final ConcurrentLinkedDeque<ForestCookie> cookieSet = cookieMap.computeIfAbsent(
                    domain, k -> new ConcurrentLinkedDeque<>());

            for (ForestCookie savedCookie : cookieSet) {
                if (savedCookie.equals(cookie)) {
                    cookieSet.remove(savedCookie);
                    break;
                } else if (savedCookie.isExpired(currentTime)) {
                    cookieSet.remove(savedCookie);
                }
            }
            if (!cookie.isExpired(currentTime)) {
                cookieSet.add(cookie);
                size.incrementAndGet();
            }
        }
    }
    
    
    
}
