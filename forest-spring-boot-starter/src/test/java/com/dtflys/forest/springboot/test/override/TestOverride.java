package com.dtflys.forest.springboot.test.override;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.springboot.test.address.SpringAddressClient;
import com.dtflys.forest.springboot.test.address.TestAddress;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("test1")
@SpringBootTest(classes = TestOverride.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.override")
@EnableAutoConfiguration
public class TestOverride {

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
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testOverride() {
        String result = myClientImpl.test1();
        assertThat(result).isEqualTo("xxxx");
    }


}
