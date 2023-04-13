package org.dromara.forest.springboot3.test.redirect;

import org.dromara.forest.annotation.BindingVar;
import org.dromara.forest.http.ForestResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-19 1:23
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("redirect")
@SpringBootTest(classes = TestAutoRedirect.class)
@EnableAutoConfiguration
public class TestAutoRedirect {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Resource
    private SpringRedirectClient springRedirectClient;

    @BindingVar("port")
    public int getPort() {
        return server.getPort();
    }

    @Test
    public void testRedirect1() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = springRedirectClient.testRedirect1();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect3() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = springRedirectClient.testRedirect3();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(301);
        String result = response.redirectionRequest().executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


}
