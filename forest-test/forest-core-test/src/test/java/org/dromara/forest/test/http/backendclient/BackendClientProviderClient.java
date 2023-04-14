package org.dromara.forest.test.http.backendclient;

import org.dromara.forest.annotation.Address;
import org.dromara.forest.annotation.BackendClient;
import org.dromara.forest.annotation.Get;
import org.dromara.forest.backend.httpclient.HttpClient;
import org.dromara.forest.backend.okhttp3.OkHttp3;
import org.dromara.forest.http.ForestRequest;

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
