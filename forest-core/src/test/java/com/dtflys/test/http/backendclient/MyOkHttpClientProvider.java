package com.dtflys.test.http.backendclient;

import com.dtflys.forest.backend.okhttp3.OkHttpClientProvider;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class MyOkHttpClientProvider implements OkHttpClientProvider {

    private final OkHttpClient okHttpClient;

    public MyOkHttpClientProvider() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(700, TimeUnit.SECONDS)
                .readTimeout(700, TimeUnit.SECONDS)
                .writeTimeout(700, TimeUnit.SECONDS)
                .callTimeout(700, TimeUnit.SECONDS)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .build();
    }

    @Override
    public OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return okHttpClient;
    }
}
