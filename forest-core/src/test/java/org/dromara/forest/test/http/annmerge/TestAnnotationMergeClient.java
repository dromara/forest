package org.dromara.forest.test.http.annmerge;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.dromara.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class TestAnnotationMergeClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static AnnotationMergeClient annotationMergeClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();

    }

    public TestAnnotationMergeClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        annotationMergeClient = configuration.createInstance(AnnotationMergeClient.class);
    }

    @Test
    public void testMyHeaders() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = annotationMergeClient.testMyHeaders(server.getPort());
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        mockRequest(server)
                .assertPathEquals("/")
                .assertHeaderEquals("Accept", "text/plain")
                .assertHeaderEquals("Content-Type", "application/json");
    }
}
