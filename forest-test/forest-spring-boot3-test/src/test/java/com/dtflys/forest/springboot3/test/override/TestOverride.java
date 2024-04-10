package com.dtflys.forest.springboot3.test.override;

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
@ActiveProfiles("test2")
@SpringBootTest
@ContextConfiguration(classes = TestOverride.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot3.test.override")
@EnableAutoConfiguration
public class TestOverride extends BaseSpringBootTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private MyClient myClient;

    @Resource
    private MyClientImpl myClientImpl;

    @BindingVar("port")
    public int getPort() {
        return server.getPort();
    }

    @Test
    public void test1() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = myClient.test1();
        assertThat(result).isNotNull().isEqualTo("Global: " + EXPECTED);
    }

    @Test
    public void testOverride() {
        String result = myClientImpl.test1();
        assertThat(result).isEqualTo("xxxx");
    }


}
