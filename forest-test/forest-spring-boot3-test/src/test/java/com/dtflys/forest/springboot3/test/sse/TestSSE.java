package com.dtflys.forest.springboot3.test.sse;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.http.ForestSSE;
import jakarta.annotation.Resource;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("sse")
@SpringBootTest(classes = TestSSE.class)
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.dtflys.forest.springboot3.test.sse"})
public class TestSSE {

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private MySSEClient sseClient;
    
    @BindingVar
    public Integer getPort() {
        return server.getPort();
    }
    
    @Test
    public void testSSE() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n"
        ));
        
        StringBuilder builder = new StringBuilder();

        sseClient.testSSE()
                .addOnData((eventSource, name, value) -> {
                    builder.append("Receive ").append(name).append(": ").append(value).append("\n");
                })
                .listen();
        
        assertThat(builder.toString()).isEqualTo(
                "Receive data: start\n" +
                "Receive data: hello\n"
        );
    }


    @Test
    public void testSSE_withInterceptor() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n"
        ));

        ForestSSE sse = sseClient.testSSE_withInterceptor().listen();

        assertThat(sse.getRequest().getAttachment("text").toString()).isEqualTo(
                "onSuccess\n" +
                "afterExecute\n" +
                "Receive name=data; value=start; comp=test\n" +
                "Receive name=data; value=hello; comp=test\n"
        );
    }

}
