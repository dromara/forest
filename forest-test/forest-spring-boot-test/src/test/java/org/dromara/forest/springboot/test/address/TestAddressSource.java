package org.dromara.forest.springboot.test.address;

import org.dromara.forest.http.ForestRequest;
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
@ActiveProfiles("addresssource")
@SpringBootTest(classes = TestAddressSource.class)
@ComponentScan(basePackages = "org.dromara.forest.springboot.test.address")
@EnableAutoConfiguration
public class TestAddressSource {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private MyAddressSource myAddressSource;

    @Resource
    private SpringAddressClient springAddressClient;


    @Test
    public void testHost() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        myAddressSource.setPort(server.getPort());
        ForestRequest<String> request = springAddressClient.testHost();
        assertThat(request.url().getScheme()).isEqualTo("https");
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        request.url().setScheme("http");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }



}
