package com.dtflys.forest.example;

import com.google.common.collect.Lists;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class ChatGPTExampleApplication implements CommandLineRunner {

    @Resource
    private ChatGPT chatGPT;
    
    
    private void testGPT() {
        GPTResponse response = chatGPT.send("你好");
        System.out.println(response.getChoices().get(0).getText());
    }
    
    
    private void testGPT4o() {
        GPT4oMessage message = new GPT4oMessage();
        message.setRole("user");
        message.setContent("Hi，你好");
        
        GPT4oBody body = new GPT4oBody();
        body.setModel("gpt-4o-mini");
        body.setMessages(Lists.newArrayList(message));
        body.setTemperature(0.7F);
        
        String response = chatGPT.send4o(body);
        System.out.println(response);
    }

    @Override
    public void run(String... args) throws Exception {
        testGPT4o();
    }
    
    
    

    public static void main(String[] args) {
        try {
            SpringApplication.run(ChatGPTExampleApplication.class, args);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}
