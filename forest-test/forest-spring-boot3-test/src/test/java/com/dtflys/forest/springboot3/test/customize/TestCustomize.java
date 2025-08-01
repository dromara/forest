package com.dtflys.forest.springboot3.test.customize;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.config.ForestConfiguration;
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


import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("customize")
@SpringBootTest(classes = TestCustomize.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.customize")
@EnableAutoConfiguration
public class TestCustomize {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private ForestConfiguration configuration;

    @Resource
    private MyCustomizedClient myCustomizedClient;

    @BindingVar("port")
    public int getPort() {
        return server.getPort();
    }


    @Test
    public void testConfiguration() {
        assertThat(configuration.getVariable("myAccept")).isNotNull();
        assertThat(configuration.getVariable("foo")).isNotNull();
    }

    @Test
    public void testHeaders() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        myCustomizedClient.testHeaders();
    }
}
