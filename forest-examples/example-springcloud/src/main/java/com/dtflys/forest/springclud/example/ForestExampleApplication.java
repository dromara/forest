package com.dtflys.forest.springclud.example;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author changjin wei(魏昌进)
 * @since 1.5.33
 */
@SpringBootApplication
@EnableDiscoveryClient
@ForestScan("com.dtflys.forest.springclud.example.client")
public class ForestExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForestExampleApplication.class, args);
    }
}
