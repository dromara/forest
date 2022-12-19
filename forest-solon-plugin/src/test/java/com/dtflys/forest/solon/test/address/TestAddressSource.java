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
@SolonTest(env = "addresssource")
public class TestAddressSource {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private MyAddressSource myAddressSource;

    @Inject
    private SpringAddressClient springAddressClient;


    @Test
    public void testHost() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        myAddressSource.setPort(server.getPort());
        ForestRequest<String> request = springAddressClient.testHost();
        assertThat(request.url().getScheme()).isEqualTo("http");
        assertThat(request.getHost()).isEqualTo("127.0.0.1");
        assertThat(request.getPort()).isEqualTo(server.getPort());
        request.url().setScheme("http");
        String result = request.executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }
}
