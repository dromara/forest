package com.dtflys.forest.http.cookie;

import com.dtflys.forest.http.*;
import com.dtflys.forest.utils.URLUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryCookieStorage implements ForestCookieStorage {
    
    private final Map<String, ConcurrentLinkedDeque<ForestCookie>> cookieMap = new ConcurrentHashMap<>();
    
    private AtomicInteger size = new AtomicInteger(0);
    
    private final int threshold;

    private final int maxSize;


    public MemoryCookieStorage(int maxSize) {
        this.maxSize = maxSize;
        this.threshold = Math.min(Math.max(maxSize / 2, 8), this.maxSize);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, TimeUnit.SECONDS.toSeconds(5));
    }

    private void refresh() {
        for (final ConcurrentLinkedDeque<ForestCookie> cookieSet : cookieMap.values()) {
            final Set<ForestCookie> toRemove = new HashSet<>();
            final long currentTime = System.currentTimeMillis();
            for (final ForestCookie cookie : cookieSet) {
                if (cookie.isExpired(currentTime)) {
                    toRemove.add(cookie);
                }
            }
            if (!toRemove.isEmpty()) {
                cookieSet.removeAll(toRemove);
                size.addAndGet(-1 * cookieSet.size());
            }
        }
        int total = 0;
        for (final ConcurrentLinkedDeque<ForestCookie> cookieSet : cookieMap.values()) {
            total += cookieSet.size();
        }
        size.set(total);
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
                size.addAndGet(-1 * cookieSet.size());
            }
        }
        return cookies;
    }

    
    private List<ConcurrentLinkedDeque<ForestCookie>> getCookieSetsByURL(ForestURL url) {
        List<ConcurrentLinkedDeque<ForestCookie>> result = new ArrayList<>();
        final String domain = url.getHost();
        final String[] fragments = domain.split("\\.");
        if (fragments.length == 1 && Character.isLetter(domain.charAt(0))) {
            final ConcurrentLinkedDeque<ForestCookie> cookieSet = getCookieSetByDomainFragments(fragments, 0);
            if (cookieSet != null) {
                result.add(cookieSet);
            }
        } else if (!URLUtils.isValidIPAddress(domain)) {
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
                    size.decrementAndGet();
                    break;
                } else if (savedCookie.isExpired(currentTime)) {
                    cookieSet.remove(savedCookie);
                    size.decrementAndGet();
                }
            }
            if (!cookie.isExpired(currentTime)) {
                cookieSet.add(cookie);
                size.incrementAndGet();
            }
        }
    }

    @Override
    public void clear() {
        size.set(0);
        cookieMap.clear();
    }


}
