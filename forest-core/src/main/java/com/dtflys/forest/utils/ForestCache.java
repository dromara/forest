package com.dtflys.forest.utils;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ForestCache<K, V> {

    private final Cache<K, V> cache;

    public ForestCache(long maxSize, Duration expireAfterAccess) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(expireAfterAccess)
                .build();
    }

    public ForestCache(long maxSize, long expireAfterAccess, TimeUnit timeUnit) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(expireAfterAccess, timeUnit)
                .build();
    }

    public ForestCache(long maxSize) {
        this.cache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .build();
    }

    public int size() {
        return (int) cache.estimatedSize();
    }

    public long evictionCount() {
        return cache.stats().evictionCount();
    }


    public boolean isEmpty() {
        return cache.estimatedSize() == 0;
    }

    public boolean containsKey(K key) {
        return cache.asMap().containsKey(key);
    }

    public boolean containsValue(V value) {
        return cache.asMap().containsValue(value);
    }

    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }


    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        cache.putAll(m);
    }

    public void clear() {
        cache.cleanUp();
    }

    public Map<K, V> toMap() {
        return cache.asMap();
    }

    public Set<K> keySet() {
        return cache.asMap().keySet();
    }

    public Collection<V> values() {
        return cache.asMap().values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return cache.asMap().entrySet();
    }
}
