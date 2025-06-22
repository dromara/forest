package com.dtflys.forest.test.http.response;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.UnclosedResponse;
import com.dtflys.forest.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
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

    public TestResponseClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        responseClient = configuration.client(ResponseClient.class);
    }

    @Test
    public void testHeaders() {
        server.enqueue(new MockResponse()
                .addHeader("Set-Cookie", "A=1; path=/; domain=localhost; expires= Wed, 19 OCT 2055 23:12:40 GMT; secure")
                .addHeader("Set-Cookie", "B=2; path=/; domain=localhost; expires= Wednesday, 19-OCT-2055 23:12:40 GMT; secure")
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

    @Test
    public void testUnclosed() {
        server.enqueue(new MockResponse()
                .addHeader("Set-Cookie", "A=1; path=/; domain=localhost; expires= Wed, 19 OCT 2055 23:12:40 GMT; secure")
                .addHeader("Set-Cookie", "B=2; path=/; domain=localhost; expires= Wednesday, 19-OCT-2055 23:12:40 GMT; secure")
                .setBody(EXPECTED));
        UnclosedResponse<String> response = responseClient.getUnclosedResponseHeaders();
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
        assertThat(response.isClosed()).isFalse();
        assertThat(response.getResult()).isEqualTo(EXPECTED);
        assertThat(response.isClosed()).isTrue();
    }

    @Test
    public void testUnclosed2() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED));
        UnclosedResponse<String> response = responseClient.getUnclosedResponseHeaders();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isClosed()).isFalse();
        assertThat(response.get(String.class)).isEqualTo(EXPECTED);
        assertThat(response.isClosed()).isTrue();
    }

    @Test
    public void testUnclosed3() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED));
        UnclosedResponse<String> response = responseClient.getUnclosedResponseHeaders();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isClosed()).isFalse();
        response.close();
        assertThat(response.isClosed()).isTrue();
    }


    @Test
    public void testUnclosed4() {
        server.enqueue(new MockResponse()
                .setBody(EXPECTED));
        UnclosedResponse<String> ref = null;
        try (UnclosedResponse<String> response = responseClient.getUnclosedResponseHeaders()) {
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.isClosed()).isFalse();
            ref = response;
        }
        assertThat(ref).isNotNull();
        assertThat(ref.isClosed()).isTrue();
    }


}
