package com.dtflys.forest.forestspringboot3example.controller;

import com.dtflys.forest.forestspringboot3example.client.SseClient;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.sse.SSEState;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;

@RestController
public class TestSSEController {
    
    private static Logger log = LoggerFactory.getLogger(TestSSEController.class);
    
    @Resource
    private SseClient sseClient;
    
    private volatile ForestSSE sse;

    @GetMapping(value = "/sse", produces = "text/event-stream")
    public Flux<String> stream() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "currentTime: " + LocalTime.now())
                .log();
    }

    @GetMapping(value = "/sse-test")
    public String testStream() {
        if (sse == null) {
            sse = sseClient.streamWithMyHandler()
                    .asyncListen();
        }
        return "ok";
    }

    @GetMapping(value = "/sse-close")
    public String testClose() {
        if (sse != null) {
            sse.close();
            sse = null;
        }
        return "ok";
    }

    @GetMapping(value = "/sse-handler-test")
    public String testStreamWithMyHandler() {
        sseClient.streamWithMyHandler().listen();
        return "ok";
    }


}
