package com.dtflys.forest.solon.test.address;

import com.dtflys.forest.http.ForestRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "address")
public class TestAddress {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private SpringAddressClient springAddressClient;

    @Inject
    private SpringNoAddressClient springNoAddressClient;

    @Inject
    private SpringBaseAddressClient springBaseAddressClient;


    @Test
    public void testHost() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest<String> request = springAddressClient.testHost();
        assertThat(request.getScheme()).isEqualTo("http");
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
