package org.dromara.forest.core.test.http;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.client.CallbackClient;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-06 15:56
 */
public class TestCallbackClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static CallbackClient callbackClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();

    }

    @Override
    public void afterRequests() {
    }

    public TestCallbackClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        callbackClient = configuration.createInstance(CallbackClient.class);
    }

    @Test
    public void testGetOnSuccess() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicBoolean atomic = new AtomicBoolean(false);
        assertThat(callbackClient.getOnSuccess("foo", (data, request, response) -> {
            assertThat(data).isNotNull().isEqualTo(EXPECTED);
            atomic.set(true);
        }))
            .isNotNull()
            .isEqualTo(EXPECTED);
        assertThat(atomic.get()).isTrue();
    }


    @Test
    public void testGetOnSuccessMap() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        AtomicBoolean atomic = new AtomicBoolean(false);
        assertThat(callbackClient.getOnSuccessMap("foo", (data, request, response) -> {
            assertThat(data)
                    .isNotNull()
                    .extracting("status")
                    .isEqualTo("ok");
            atomic.set(true);
        }))
            .isNotNull()
            .isEqualTo(EXPECTED);
        assertThat(atomic.get()).isTrue();
    }

}
