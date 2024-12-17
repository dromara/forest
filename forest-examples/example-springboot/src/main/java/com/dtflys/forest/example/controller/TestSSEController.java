package com.dtflys.forest.example.controller;

import com.dtflys.forest.example.client.SseClient;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.sse.EventSource;
import com.dtflys.forest.sse.SSEStringMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class TestSSEController {
    
    private static Logger log = LoggerFactory.getLogger(TestSSEController.class);
    
    @Resource
    private SseClient sseClient;
    @Autowired
    private SchedulingTaskExecutor schedulingTaskExecutor;

    @GetMapping(value = "/sse", produces = "text/event-stream")
    public SseEmitter stream() {
        SseEmitter sseEmitter = new SseEmitter(30000L);
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    sseEmitter.send("send " + i);
                    TimeUnit.SECONDS.sleep(1);
                }
                sseEmitter.complete();
            } catch (Exception e) {
                e.printStackTrace();
                sseEmitter.completeWithError(e);
            }
        });
        return sseEmitter;
    }
    
    @GetMapping(value = "/sse-test")
    public String testStream() {
        sseClient.stream()
                .addOnData((eventSource, name, value) -> {
                    log.info("Received event [{}: {}]", name, value);
                })
                .setOnClose((eventSource, res) -> {
                    log.info("SSE Closed");
                })
                .listen();
        return "ok";
    }

    @GetMapping(value = "/sse-handler-test")
    public String testStreamWithMyHandler() {
        sseClient.streamWithMyHandler().listen();
        return "ok";
    }


}
