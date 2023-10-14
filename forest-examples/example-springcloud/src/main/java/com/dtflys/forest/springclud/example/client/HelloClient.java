package com.dtflys.forest.springclud.example.client;

import com.dtflys.forest.annotation.Address;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.springcloud.LoadBalancer;

import java.util.Map;

/**
 * @author changjin wei(魏昌进)
 * @since 1.5.33
 */
public interface HelloClient {

    @Get("http://localhost:8080/hello/hello")
    @LoadBalancer("example-springcloud")
    Map<String, String> hello();


    @Get("/hello/hello")
    @LoadBalancer("example-springcloud")
    @Address(host = "localhost", port = "8080")
    Map<String, String> loadBalancer_address();

}