package com.dtflys.forest.utils;


import cn.hutool.cache.impl.LRUCache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ForestCache<K, V> {

    
    private final LRUCache<K, V> lruCache;

    public ForestCache(long maxSize, Duration expireAfterAccess) {
        this.lruCache = new LRUCache<>((int) maxSize, expireAfterAccess.toMillis());
    }

    public ForestCache(long maxSize, long expireAfterAccess, TimeUnit timeUnit) {
        this.lruCache = new LRUCache<>((int) maxSize, timeUnit.toMillis(expireAfterAccess));
    }

    public ForestCache(long maxSize) {
        this.lruCache = new LRUCache<>((int) maxSize);
    }

    public int size() {
        return lruCache.size();
    }



    public boolean isEmpty() {
        return lruCache.isEmpty();
    }

    public boolean containsKey(K key) {
        return lruCache.containsKey(key);
    }


    public V get(K key) {
        return lruCache.get(key);
    }

    public V get(K key, Supplier<? extends V> supplier) {
        return lruCache.get(key,  true, () -> supplier.get());
    }

    public void put(K key, V value) {
        lruCache.put(key, value);
    }


    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            lruCache.put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        lruCache.clear();
    }

    public Map<K, V> toMap() {
        Map<K, V> map = new HashMap<>(lruCache.size());
        for (K key : lruCache.keySet()) {
            map.put(key, lruCache.get(key));
        }
        return map;
    }

    public Set<K> keySet() {
        return lruCache.keySet();
    }

    public Collection<V> values() {
        Collection<V> values = new ArrayList<>(lruCache.size());
        for (K key : lruCache.keySet()) {
            values.add(lruCache.get(key));
        }
        return values;
    }
    
}
