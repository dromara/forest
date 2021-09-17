package com.dtflys.test.http.redirect;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestRedirectClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private RedirectClient redirectClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }


    public TestRedirectClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        redirectClient = configuration.createInstance(RedirectClient.class);
    }

    @Test
    public void testRedirect_301() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = redirectClient.testRedirect();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(301);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect_302() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(302));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = redirectClient.testRedirect();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(302);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect_303() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(303));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = redirectClient.testRedirect();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(303);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect_304() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(304));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = redirectClient.testRedirect();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(304);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect_305() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(305));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = redirectClient.testRedirect();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(305);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect_306() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(306));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = redirectClient.testRedirect();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(306);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect_307() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(307));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = redirectClient.testRedirect();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(307);
        assertThat(response.isRedirection()).isTrue();
        assertThat(response.getRedirectionLocation()).isEqualTo("http://localhost:" + server.getPort() + "/b");
        String result = response.redirectionRequest().execute(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

}
