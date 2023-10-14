package com.dtflys.forest.springclud.example.controller;

import com.dtflys.forest.springclud.example.client.HelloClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author changjin wei(魏昌进)
 * @since 1.5.33
 */
@RestController
@RequestMapping("hello")
public class HelloController {

    @Autowired
    private HelloClient helloClient;

    @GetMapping("hello")
    public Map<String, String> hello() {
        Map<String, String> hello = new HashMap<>();
        hello.put("hello", "hello");
        return hello;
    }

    @GetMapping("loadBalancer")
    public Map<String, String> loadBalancer() {
        Map<String, String> hello = new HashMap<>(helloClient.hello());
        hello.put("Spring Cloud", "Spring Cloud");
        return hello;
    }

    @GetMapping("loadBalancer_address")
    public Map<String, String> loadBalancer_address() {
        Map<String, String> hello = new HashMap<>(helloClient.loadBalancer_address());
        hello.put("Spring Cloud", "Spring Cloud");
        return hello;
    }

}
