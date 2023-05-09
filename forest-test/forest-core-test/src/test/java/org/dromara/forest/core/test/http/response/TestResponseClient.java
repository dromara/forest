package org.dromara.forest.core.test.http.response;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.BaseClientTest;
import org.dromara.forest.http.ForestCookie;
import org.dromara.forest.http.ForestResponse;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;
import java.util.List;

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
                .addHeader("Set-Cookie", "A=1; path=/; domain=localhost; expires= Wednesday, 19-OCT-2105 23:12:40 GMT; secure")
                .addHeader("Set-Cookie", "B=2; path=/; domain=localhost; expires= Wednesday, 19-OCT-2105 23:12:40 GMT; secure")
                .setBody(EXPECTED));
        ForestResponse<String> response = responseClient.getResponseHeaders();
        assertThat(response.isSuccess()).isTrue();
        List<ForestCookie> cookies = response.getCookies();
        assertThat(cookies).isNotNull();
        assertThat(cookies.size()).isEqualTo(2);
        assertThat(cookies.get(0).getName()).isEqualTo("A");
        assertThat(cookies.get(0).getValue()).isEqualTo("1");
        assertThat(cookies.get(0).getPath()).isEqualTo("/");
        assertThat(cookies.get(0).isSecure()).isTrue();
        assertThat(cookies.get(0).isExpired(new Date())).isFalse();
        assertThat(cookies.get(1).getName()).isEqualTo("B");
        assertThat(cookies.get(1).getValue()).isEqualTo("2");
        assertThat(cookies.get(1).getPath()).isEqualTo("/");
        assertThat(cookies.get(1).isSecure()).isTrue();
        assertThat(cookies.get(1).isExpired(new Date())).isFalse();
    }

}
