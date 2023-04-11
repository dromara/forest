package com.dtflys.forest.springboot.test.redirect;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.http.ForestResponse;
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
@ActiveProfiles("notredirect")
@SpringBootTest(classes = TestNotAutoRedirect.class)
@EnableAutoConfiguration
public class TestNotAutoRedirect {

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
        assertThat(response.getStatusCode()).isEqualTo(301);
        assertThat(response.isRedirection()).isTrue();
        String result = response.redirectionRequest().executeAsString();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }

    @Test
    public void testRedirect2() {
        server.enqueue(new MockResponse()
                .addHeader("Location", "http://localhost:" + server.getPort() + "/b")
                .setResponseCode(301));
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestResponse<String> response = springRedirectClient.testRedirect2();
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(200);
        String result = response.getResult();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


}
