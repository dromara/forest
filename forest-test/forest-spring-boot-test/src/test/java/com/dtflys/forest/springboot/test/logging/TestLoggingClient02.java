package com.dtflys.forest.springboot.test.logging;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogHandler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging02")
@SpringBootTest(classes = TestLoggingClient02.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.logging")
@EnableAutoConfiguration
public class TestLoggingClient02 {

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
        assertThat(configuration.isLogRequestBody()).isTrue();
        assertThat(configuration.isLogResponseStatus()).isTrue();
        assertThat(configuration.isLogResponseHeaders()).isTrue();
        assertThat(configuration.isLogResponseContent()).isTrue();
    }

    @Test
    public void testLogging2() {
        ForestLogHandler oldLogHandler = configuration.getLogHandler();
        try {
            DefaultLogHandler logHandler = Mockito.spy(new DefaultLogHandler());
            configuration.setLogHandler(logHandler);
            server.enqueue(new MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setBody(EXPECTED));
            ForestResponse response = loggingClient.testLogging();

            Mockito.verify(logHandler).logContent(eq("Request (okhttp3): \n" +
                    "\tGET http://" + server.getHostName() + ":" + server.getPort() + "/ HTTP\n" +
                    "\tHeaders: \n" +
                    "\t\tUser-Agent: forest/dev"));

            Mockito.verify(logHandler).logContent(eq("Response: \n" +
                    "\tResponse Status:\n" +
                    "\t\tStatus = 200, Time = " + response.getTimeAsMillisecond() + "ms\n" +
                    "\tResponse Headers:\n" +
                    "\t\tcontent-length: 16\n" +
                    "\t\tcontent-type: application/json"));

            Mockito.verify(logHandler).logContent(eq("Response Content:\n" +
                    "\t{\"status\": \"ok\"}"));
        } finally {
            configuration.setLogHandler(oldLogHandler);
        }
    }

}
