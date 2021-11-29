package com.dtflys.forest.springboot.test.async;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.backend.AsyncHttpExecutor;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.springboot.test.address.TestAddress;
import com.dtflys.forest.springboot.test.binding.BindingVarClient;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("async")
@SpringBootTest(classes = TestAddress.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.async")
@EnableAutoConfiguration
public class TestAsync {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private AsyncClient asyncClient;

    @BindingVar("port")
    public int getPort() {
        return server.getPort();
    }

    @Resource
    private ForestConfiguration configuration;

    @Test
    public void testConfig() {
        assertThat(configuration.getMaxAsyncThreadSize()).isEqualTo(300);
        assertThat(AsyncHttpExecutor.getMaxAsyncThreadSize()).isEqualTo(300);
    }
}
