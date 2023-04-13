package org.dromara.forest.springboot3.test.array;

import org.dromara.forest.annotation.BindingVar;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("array")
@SpringBootTest(classes = TestArrayClient.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.array")
@EnableAutoConfiguration
public class TestArrayClient {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private ArrayClient arrayClient;

    @BindingVar("port")
    public int port() {
        return server.getPort();
    }

    @Test
    public void testArrayFromProperties() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = arrayClient.arrayQueryFromProperties();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


}
