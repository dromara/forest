package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.callback.OnSaveCookie;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestCookies;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.CookieClient;
import com.dtflys.test.mock.CookieMockServer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-12-14 1:04
 */
public class TestCookieClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private CookieClient cookieClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    @Override
    public void afterRequests() {
    }

    public TestCookieClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        cookieClient = configuration.createInstance(CookieClient.class);
    }

    @Test
    public void testCookieWithCallback() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(EXPECTED)
                .setHeader(HttpHeaders.ACCEPT, "text/plain")
                .setHeader("Set-Cookie", "cookie_foo=cookie_bar"));
        AtomicReference<ForestCookie> cookieAtomic = new AtomicReference<>(null);
        cookieClient.testLoginWithCallback((request, cookies) -> cookieAtomic.set(cookies.allCookies().get(0)));
        ForestCookie cookie = cookieAtomic.get();
        assertThat(cookie)
                .isNotNull()
                .extracting(
                        ForestCookie::getDomain,
                        ForestCookie::getPath,
                        ForestCookie::getName,
                        ForestCookie::getValue)
                .contains("localhost", "/", "cookie_foo", "cookie_bar");
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/login");

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(EXPECTED)
                .setHeader(HttpHeaders.ACCEPT, "text/plain"));

        assertThat(cookieClient.testCookieWithCallback((request, cookies) -> {
            cookies.addCookie(cookie);
        }))
            .isNotNull()
            .extracting(ForestResponse::getStatusCode, ForestResponse::getResult)
            .contains(200, EXPECTED);
        mockRequest(server)
            .assertMethodEquals("POST")
            .assertPathEquals("/test")
            .assertHeaderEquals("Cookie", "cookie_foo=cookie_bar");
    }

    @Test
    public void testCookieWithInterceptor() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(EXPECTED)
                .setHeader(HttpHeaders.ACCEPT, "text/plain")
                .setHeader("Set-Cookie", "cookie_foo=cookie_bar"));
        cookieClient.testLoginWithInterceptor();
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/login");

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(EXPECTED)
                .setHeader(HttpHeaders.ACCEPT, "text/plain"));
        assertThat(cookieClient.testCookieWithInterceptor())
            .isNotNull()
            .extracting(ForestResponse::getStatusCode, ForestResponse::getResult)
            .contains(200, EXPECTED);
    }


}
