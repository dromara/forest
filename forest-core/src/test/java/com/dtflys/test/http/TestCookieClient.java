package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.ForestCookie;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.CookieClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;

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
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    public TestCookieClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
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
        AtomicInteger saveCount = new AtomicInteger(0);
        AtomicInteger loadCount = new AtomicInteger(0);
        ForestResponse response = cookieClient.testLoginWithCallback((request, cookies) -> {
                cookieAtomic.set(cookies.allCookies().get(0));
                saveCount.incrementAndGet();
        });
        assertThat(response).isNotNull();
        assertThat(response.getCookies()).isNotNull();
        assertThat(response.getCookies().size()).isEqualTo(1);
        ForestCookie resCookie = response.getCookie("cookie_foo");
        assertThat(resCookie).isNotNull();
        assertThat(resCookie.getName()).isEqualTo("cookie_foo");
        assertThat(resCookie.getValue()).isEqualTo("cookie_bar");
        assertThat(resCookie.getDomain()).isNotNull().isEqualTo("localhost");
        assertThat(saveCount.get()).isEqualTo(1);

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
            cookies.addCookie(new ForestCookie("attr1", "foo"));
            cookies.addCookie(new ForestCookie("attr2", "bar"));
            cookies.addCookie(new ForestCookie("attr3", "foobar").setDomain("baidu.com"));
            ForestCookie otherDomainCookie = new ForestCookie("name", "otherDomain");
            otherDomainCookie.setDomain("baidu.com");
            cookies.addCookie(otherDomainCookie);
            ForestCookie otherPathCookie = new ForestCookie("name", "otherPath");
            otherPathCookie.setPath("/xxx/");
            cookies.addCookie(otherPathCookie);
            ForestCookie pathCookie = new ForestCookie("name", "path");
            pathCookie.setPath("/test");
            cookies.addCookie(pathCookie);
            ForestCookie pathCookie2 = new ForestCookie("name", "path2");
            pathCookie2.setPath("/test/");
            cookies.addCookie(pathCookie2);
            loadCount.incrementAndGet();
        }))
            .isNotNull()
            .extracting(ForestResponse::getStatusCode, ForestResponse::getResult)
            .contains(200, EXPECTED);
        assertThat(loadCount.get()).isEqualTo(1);
        mockRequest(server)
            .assertMethodEquals("POST")
            .assertPathEquals("/test/xxx")
            .assertHeaderEquals("Cookie", "cookie_foo=cookie_bar; attr1=foo; attr2=bar; name=path; name=path2");
    }

    @Test
    public void testCookieWithCallback_not_strict() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(EXPECTED)
                .setHeader(HttpHeaders.ACCEPT, "text/plain")
                .setHeader("Set-Cookie", "cookie_foo=cookie_bar"));
        AtomicReference<ForestCookie> cookieAtomic = new AtomicReference<>(null);
        AtomicInteger saveCount = new AtomicInteger(0);
        AtomicInteger loadCount = new AtomicInteger(0);
        ForestResponse response = cookieClient.testLoginWithCallback((request, cookies) -> {
            cookieAtomic.set(cookies.allCookies().get(0));
            saveCount.incrementAndGet();
        });
        assertThat(response).isNotNull();
        assertThat(response.getCookies()).isNotNull();
        assertThat(response.getCookies().size()).isEqualTo(1);
        ForestCookie resCookie = response.getCookie("cookie_foo");
        assertThat(resCookie).isNotNull();
        assertThat(resCookie.getName()).isEqualTo("cookie_foo");
        assertThat(resCookie.getValue()).isEqualTo("cookie_bar");
        assertThat(resCookie.getDomain()).isNotNull().isEqualTo("localhost");
        assertThat(saveCount.get()).isEqualTo(1);

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
            cookies.strict(false)
                    .addCookie(cookie)
                    .addCookie(new ForestCookie("attr1", "foo"))
                    .addCookie(new ForestCookie("attr2", "bar"))
                    .addCookie(new ForestCookie("attr3", "foobar").setDomain("xxx.com"));
            ForestCookie otherDomainCookie = new ForestCookie("name", "otherDomain");
            otherDomainCookie.setDomain("baidu.com");
            cookies.addCookie(otherDomainCookie);
            ForestCookie otherPathCookie = new ForestCookie("name", "otherPath");
            otherPathCookie.setPath("/xxx/");
            cookies.addCookie(otherPathCookie);
            ForestCookie pathCookie = new ForestCookie("name", "path");
            pathCookie.setPath("/test");
            cookies.addCookie(pathCookie);
            ForestCookie pathCookie2 = new ForestCookie("name", "path2");
            pathCookie2.setPath("/test/");
            cookies.addCookie(pathCookie2);
            loadCount.incrementAndGet();
        }))
                .isNotNull()
                .extracting(ForestResponse::getStatusCode, ForestResponse::getResult)
                .contains(200, EXPECTED);
        assertThat(loadCount.get()).isEqualTo(1);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/test/xxx")
                .assertHeaderEquals("Cookie", "cookie_foo=cookie_bar; attr1=foo; attr2=bar; attr3=foobar; name=otherDomain; name=otherPath; name=path; name=path2");
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


    @Test
    public void testInvalidCookie() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(EXPECTED)
                .setHeader(HttpHeaders.ACCEPT, "text/plain")
                .setHeader("Set-Cookie", "xxxx"));
        ForestResponse response = cookieClient.testInvalidCookie();
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/test");
        assertThat(response.getCookies().size()).isEqualTo(0);
    }

    @Test
    public void testInvalidCookie2() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(EXPECTED)
                .setHeader(HttpHeaders.ACCEPT, "text/plain")
                .setHeader("Set-Cookie", ""));
        ForestResponse response = cookieClient.testInvalidCookie();
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/test");
        assertThat(response.getCookies().size()).isEqualTo(0);
    }


}
