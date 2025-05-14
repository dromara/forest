package com.dtflys.forest.test.utils;

import com.dtflys.forest.utils.ForestCache;
import org.junit.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCache {

    @Test
    public void testCacheMaxSize() throws InterruptedException {
        ForestCache<Integer, Integer> cache = new ForestCache<>(10);
        assertThat(cache.size()).isEqualTo(0);
        for (int i = 0; i < 30; i++) {
            cache.put(i, i);
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println("size: " + cache.size());
        assertThat(cache.size()).isEqualTo(10);
    }

    @Test
    public void testCacheExpireTimeAfterRead() throws InterruptedException {
        ForestCache<Integer, Integer> cache = new ForestCache<>(10, 1, TimeUnit.SECONDS);
        assertThat(cache.size()).isEqualTo(0);
        for (int i = 0; i < 30; i++) {
            cache.put(i, i);
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println("size: " + cache.size());
        assertThat(cache.size()).isEqualTo(10);
        for (int i = 0; i < 30; i++) {
            cache.get(i);
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println("size: " + cache.size());
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void testCacheExpireTimeAfterRead2() throws InterruptedException {
        ForestCache<Integer, Integer> cache = new ForestCache<>(10, Duration.ofSeconds(1));
        assertThat(cache.size()).isEqualTo(0);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            map.put(i, i);
        }
        cache.putAll(map);
        TimeUnit.SECONDS.sleep(1);
        System.out.println("size: " + cache.size());
        assertThat(cache.size()).isEqualTo(10);
        for (int i = 0; i < 30; i++) {
            cache.get(i);
        }
        TimeUnit.SECONDS.sleep(2);
        System.out.println("size: " + cache.size());
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void testCacheClear() {
        ForestCache<Integer, Integer> cache = new ForestCache<>(10);
        assertThat(cache.size()).isEqualTo(0);
        for (int i = 0; i < 30; i++) {
            cache.put(i, i);
        }
        assertThat(cache.size()).isEqualTo(10);
        cache.clear();
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void testCacheToMap() {
        ForestCache<Integer, Integer> cache = new ForestCache<>(10);
        assertThat(cache.size()).isEqualTo(0);
        for (int i = 0; i < 10; i++) {
            cache.put(i, i);
        }
        Map<Integer, Integer> map = cache.toMap();
        assertThat(map).isNotNull();
        assertThat(map.size()).isEqualTo(10);
        for (int i = 0; i < 10; i++) {
            assertThat(map.get(i)).isEqualTo(i);
        }
    }


}
