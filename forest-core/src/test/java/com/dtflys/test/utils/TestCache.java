package com.dtflys.test.utils;

import com.dtflys.forest.utils.ForestCache;
import org.junit.Test;

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


}
