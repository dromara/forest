package com.dtflys.forest.test.proxy;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.proxy.ProxyFactory;
import com.dtflys.forest.test.http.client.GetClient;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TestProxyDefaultMethods {

    private static ForestConfiguration configuration = ForestConfiguration.configuration();

    private static ProxyFactory<GetClient> getClientProxyFactory = new ProxyFactory<>(configuration, GetClient.class);

    @Test
    public void testHashCode() {
        GetClient getClient = getClientProxyFactory.createInstance();
        assertThat(getClient).isNotNull();
        assertThat(getClient.hashCode()).isNotNull().isGreaterThan(0);
    }
    
    @Test
    public void testGetClass() {
        GetClient getClient = getClientProxyFactory.createInstance();
        assertThat(getClient).isInstanceOf(GetClient.class);
        Class<?> clazz = getClient.getClass();
        assertThat(clazz).isNotNull();
        assertThat(GetClient.class.isAssignableFrom(clazz)).isTrue();
    }
    
    @Test
    public void testToString() {
        GetClient getClient = getClientProxyFactory.createInstance();
        String toString = getClient.toString();
        assertThat(toString).isNotNull().isEqualTo("{Forest Proxy Object of " + GetClient.class.getName() + "}");
    }
    
    @Test
    public void testWait() throws InterruptedException {
        GetClient getClient = getClientProxyFactory.createInstance();
        AtomicInteger count = new AtomicInteger(0);
        CompletableFuture.runAsync(() -> {
            synchronized (getClient) {
                getClient.notify();
                count.incrementAndGet();
            }
        });
        synchronized (getClient) {
            getClient.wait();
        }
        assertThat(count.get()).isEqualTo(1);
    }
}
