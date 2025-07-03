package com.dtflys.forest.test.http.cookie;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

public class TestCookieClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"1\", \"data\":\"2\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration = ForestConfiguration.createConfiguration();

    private CookieClient cookieClient;

    @Before
    public void setup() {
        configuration.getCookieStorage().clear();
    }


    public TestCookieClient(String backendName, String jsonConverterName) {
        super(backendName, jsonConverterName, configuration);
        configuration.setVariableValue("hostname", server.getHostName());
        configuration.setVariableValue("port", server.getPort());
        cookieClient = configuration.client(CookieClient.class);
    }

    @Test
    public void testLoginCookie() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED)
                .addHeader("Set-Cookie", "FOO=123-abc; Max-Age=2592000; Path=/abc; Secure; Version=1; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "BAR=789-xyz; Max-Age=2592000; Secure; HttpOnly; Version=2; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "A=1; Max-Age=2592000; Comment=XXXX; Secure; HttpOnly; Version=3"));

        ForestResponse<String> response = cookieClient.login("foo", "bar");
        assertThat(response).isNotNull();
        assertThat(response.getCookie("FOO")).isNotNull();
        assertThat(response.getCookie("FOO").getValue()).isNotNull().isEqualTo("123-abc");
        assertThat(response.getCookie("BAR")).isNotNull();
        assertThat(response.getCookie("BAR").getValue()).isNotNull().isEqualTo("789-xyz");
        assertThat(response.getCookie("A")).isNotNull();
        assertThat(response.getCookie("A").getValue()).isNotNull().isEqualTo("1");
    }

    @Test
    public void testLoginCookieAutoSaveAndLoad() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(EXPECTED)
                .addHeader("Set-Cookie", "FOO=123-abc; Max-Age=2592000; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "BAR=789-xyz; Max-Age=2592000; Domain=" + server.getHostName())
                .addHeader("Set-Cookie", "A=1; Max-Age=2592000; Domain=" + server.getHostName()));

        cookieClient.login("foo", "bar");

        mockRequest(server)
                .assertBodyEquals("username=foo&password=bar");

        server.enqueue(new MockResponse().setResponseCode(200));

        cookieClient.doSomething();

        mockRequest(server)
                .assertHeaderEquals("Cookie", "FOO=123-abc; BAR=789-xyz; A=1");
    }

}
