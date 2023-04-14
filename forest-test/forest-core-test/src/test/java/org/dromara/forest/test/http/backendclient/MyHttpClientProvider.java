package org.dromara.forest.test.http.backendclient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dromara.forest.backend.httpclient.HttpClientProvider;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;

import java.util.concurrent.atomic.AtomicInteger;

public class MyHttpClientProvider implements HttpClientProvider {

    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public HttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        count.incrementAndGet();
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(700 * 1000)
                .setSocketTimeout(700 * 1000)
                .setConnectionRequestTimeout(700 * 1000)
                .setRedirectsEnabled(false)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
        return httpClient;
    }

    public int getCount() {
        return count.get();
    }

    public void setCount(int count) {
        this.count.set(count);
    }

}
