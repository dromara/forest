package org.dromara.forest.core.test.http.backendclient;

import org.dromara.forest.backend.okhttp3.OkHttpClientProvider;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MyOkHttpClientProvider implements OkHttpClientProvider {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        count.incrementAndGet();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(700, TimeUnit.SECONDS)
                .readTimeout(700, TimeUnit.SECONDS)
                .writeTimeout(700, TimeUnit.SECONDS)
                .callTimeout(700, TimeUnit.SECONDS)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .build();
        return okHttpClient;
    }

    public int getCount() {
        return count.get();
    }

    public void setCount(int count) {
        this.count.set(count);
    }
}
