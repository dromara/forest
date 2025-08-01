package com.dtflys.forest.springboot.test.logging;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.config.ForestConfiguration;
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

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging04")
@SpringBootTest(classes = TestLoggingClient04.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.logging")
@EnableAutoConfiguration
public class TestLoggingClient04 {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private LoggingClient loggingClient;

    @Resource
    private ForestConfiguration configuration;

    @BindingVar("server")
    public MockWebServer getServer() {
        return server;
    }

    @Test
    public void testLogConfig() {
        assertThat(configuration.isLogEnabled()).isTrue();
        assertThat(configuration.isLogRequest()).isTrue();
        assertThat(configuration.isLogRequestHeaders()).isTrue();
        assertThat(configuration.isLogRequestBody()).isFalse();
        assertThat(configuration.isLogResponseStatus()).isTrue();
        assertThat(configuration.isLogResponseHeaders()).isFalse();
        assertThat(configuration.isLogResponseContent()).isFalse();
    }

    @Test
    public void testLogging4() {
        server.enqueue(new MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody(EXPECTED));
        loggingClient.testLogging();
    }

}
