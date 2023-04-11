package com.dtflys.forest.springboot3.test.when;

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

import jakarta.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("when")
@SpringBootTest(classes = TestWhen.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.when")
@EnableAutoConfiguration
public class TestWhen {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private MyWhenCondition myWhenCondition;

    @Resource
    private SpringWhenClient springWhenClient;

    @Test
    public void testSuccessWhen() {
        myWhenCondition.setSuccessInvokeCount(0);
        myWhenCondition.setRejectStatusCode(203);
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicBoolean isError = new AtomicBoolean(false);
        springWhenClient.testSuccessWhen(server.getPort(), (ex, req, res) -> {
            isError.set(true);
        });
        assertThat(isError.get()).isTrue();
        assertThat(myWhenCondition.getSuccessInvokeCount()).isEqualTo(1);
    }

    @Test
    public void testRetryWhen() {
        myWhenCondition.setRetryInvokeCount(0);
        myWhenCondition.setRetryStatusCode(203);
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        String result = springWhenClient.testRetryWhen(server.getPort(), (data, req, res) -> {
            assertThat(req.getCurrentRetryCount()).isEqualTo(3);
            isSuccess.set(true);
        });
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(myWhenCondition.getRetryInvokeCount()).isEqualTo(4);
        assertThat(isSuccess.get()).isTrue();
    }

}
