package com.dtflys.forest.springboot.test.address;

import com.dtflys.forest.http.ForestRequest;
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
@ActiveProfiles("address")
@SpringBootTest(classes = TestAddress.class)
@ComponentScan(basePackages = "com.dtflys.forest.springboot.test.address")
@EnableAutoConfiguration
public class TestAddress {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private SpringAddressClient springAddressClient;

    @Resource
    private SpringNoAddressClient springNoAddressClient;

    @Resource
    private SpringBaseAddressClient springBaseAddressClient;


    @Test
    public void testHost() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = springAddressClient.testHost();
        assertThat(request.getScheme()).isEqualTo("https");
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        String result = request
                .scheme("http")
                .port(server.getPort())
                .executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testHost2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = springNoAddressClient.testHost();
        assertThat(request.getHost()).isEqualTo("localhost");
        String result = request
                .port(server.getPort())
                .executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testBase() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = springBaseAddressClient.testBaseAddress(server.getPort());
        assertThat(request.getScheme()).isEqualTo("http");
        assertThat(request.getHost()).isEqualTo("localhost");
        String result = request
                .port(server.getPort())
                .executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


}
