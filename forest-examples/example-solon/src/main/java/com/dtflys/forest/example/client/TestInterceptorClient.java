package com.dtflys.forest.example.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.DataParam;
import com.dtflys.forest.annotation.ForestClient;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.example.interceptors.ApiClientInterceptor;


@ForestClient
@BaseRequest(baseURL = "localhost:8080")
public interface TestInterceptorClient {

    @Request(
            url = "/receive-interceptor",
            type = "post",
            dataType = "text",
            interceptor = ApiClientInterceptor.class
    )
    String testInterceptor(@DataParam("username") String username);
}
