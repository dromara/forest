package com.dtflys.forest.springboot3.test.array;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.springboot3.test.BaseSpringBootTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import jakarta.annotation.Resource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("array")
@SpringBootTest
@ContextConfiguration(classes = TestArrayClient.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot3.test.array")
@EnableAutoConfiguration
public class TestArrayClient extends BaseSpringBootTest {

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
