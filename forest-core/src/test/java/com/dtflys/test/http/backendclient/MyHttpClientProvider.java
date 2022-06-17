package com.dtflys.test.http.backendclient;

import com.dtflys.forest.backend.httpclient.HttpClientProvider;
import com.dtflys.forest.backend.okhttp3.OkHttpClientProvider;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import okhttp3.OkHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.TimeUnit;

public class MyHttpClientProvider implements HttpClientProvider {

    private final CloseableHttpClient httpClient;

    public MyHttpClientProvider() {
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setStaleConnectionCheckEnabled(true).build())
                .build();
    }

    @Override
    public HttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return httpClient;
    }
}
