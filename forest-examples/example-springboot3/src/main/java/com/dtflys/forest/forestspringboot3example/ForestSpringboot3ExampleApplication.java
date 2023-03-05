package com.dtflys.forest.forestspringboot3example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class ForestSpringboot3ExampleApplication {


    public static void main(String[] args) {
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
        SpringApplication.run(ForestSpringboot3ExampleApplication.class, args);
    }

}
