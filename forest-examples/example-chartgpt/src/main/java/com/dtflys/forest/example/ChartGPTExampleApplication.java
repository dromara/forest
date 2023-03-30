package com.dtflys.forest.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class ChartGPTExampleApplication implements CommandLineRunner {

    @Resource
    private ChartGPT chartGPT;

    @Override
    public void run(String... args) throws Exception {
        GPTResponse response = chartGPT.send("你好");
        System.out.println(response.getChoices().get(0).getText());
    }

    public static void main(String[] args) {
        try {
            SpringApplication.run(ChartGPTExampleApplication.class, args);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}
