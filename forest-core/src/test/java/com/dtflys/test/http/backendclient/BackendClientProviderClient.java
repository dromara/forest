package com.dtflys.test.http.backendclient;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.BackendClient;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.backend.httpclient.HttpClient;
import com.dtflys.forest.backend.okhttp3.OkHttp3;
import com.dtflys.forest.http.ForestRequest;

@Address(host = "localhost", port = "{port}")
public interface BackendClientProviderClient {

    @Get("/")
    @OkHttp3(client = MyOkHttpClientProvider.class)
    ForestRequest<String> getOkHttpClientProvider();

    @Get("/")
    @HttpClient(client = MyHttpClientProvider.class)
    ForestRequest<String> getHttpClientProvider();

    @Get("/")
    @OkHttp3(client = MyOkHttpClientProvider.class)
    @BackendClient(cache = false)
    ForestRequest<String> getOkHttpClientProvider_without_cache();

    @Get("/")
    @HttpClient(client = MyHttpClientProvider.class)
    @BackendClient(cache = false)
    ForestRequest<String> getHttpClientProvider_without_cache();

}
