package com.dtflys.test.http.timeout;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestTimeoutClient extends BaseClientTest {


    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private TimeoutClient timeoutClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestTimeoutClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVar("port", server.getPort());
        timeoutClient = configuration.client(TimeoutClient.class);
    }


    @Test
    public void testConnectTimeout() {
        ForestRequest request = timeoutClient.testConnectTimeout();
        assertThat(request).isNotNull();
        assertThat(request.getConnectTimeout()).isEqualTo(10);
        ForestResponse response = (ForestResponse) request.as(ForestResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.getException()).isNotNull();
        assertThat(response.isTimeout()).isTrue();
    }


    @Test
    public void testReadTimeout() {
        server.enqueue(new MockResponse().setHeadersDelay(20, TimeUnit.MILLISECONDS));
        ForestRequest request = timeoutClient.testReadTimeout();
        assertThat(request).isNotNull();
        assertThat(request.getReadTimeout()).isEqualTo(10);
        ForestResponse response = (ForestResponse) request.as(ForestResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.isTimeout()).isTrue();
    }

}
