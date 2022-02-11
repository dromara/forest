package com.dtflys.test.http.response;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestHeaderMap;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestResponseClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private ResponseClient responseClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestResponseClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        responseClient = configuration.client(ResponseClient.class);
    }

    @Test
    public void testHeaders() {
        server.enqueue(new MockResponse()
                .addHeader("Set-Cookie", "A=1; path=/; domain=localhost; expires= Wednesday, 19-OCT-05 23:12:40 GMT; [secure]")
                .addHeader("Set-Cookie", "B=2; path=/; domain=localhost; expires= Wednesday, 19-OCT-05 23:12:40 GMT; [secure]")
                .setBody(EXPECTED));
        ForestResponse<String> response = responseClient.getResponseHeaders();
        assertThat(response.isSuccess()).isTrue();
        ForestHeaderMap headers = response.getHeaders();
        assertThat(headers).isNotNull();
        assertThat(headers.size()).isGreaterThan(2);
        List<String> values = headers.getValues("Set-Cookie");
        assertThat(values.size()).isEqualTo(2);
        assertThat(values.get(0)).isEqualTo("A=1; path=/; domain=localhost; expires= Wednesday, 19-OCT-05 23:12:40 GMT; [secure]");
        assertThat(values.get(1)).isEqualTo("B=2; path=/; domain=localhost; expires= Wednesday, 19-OCT-05 23:12:40 GMT; [secure]");
    }

}
