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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-12-14 1:04
 */
public class TestCookieClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    @Rule
    public CookieMockServer server = new CookieMockServer(this);

    private static ForestConfiguration configuration;

    private CookieClient cookieClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", CookieMockServer.port);
    }


    public TestCookieClient(HttpBackend backend) {
        super(backend, configuration);
        cookieClient = configuration.createInstance(CookieClient.class);
    }



    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testCookieWithCallback() {
        AtomicReference<ForestCookie> cookieAtomic = new AtomicReference<>(null);
        cookieClient.testLoginWithCallback((request, cookies) -> cookieAtomic.set(cookies.allCookies().get(0)));
        ForestCookie cookie = cookieAtomic.get();
        assertNotNull(cookie);
        assertEquals("localhost", cookie.getDomain());
        assertEquals("/", cookie.getPath());
        assertEquals("cookie_foo", cookie.getName());
        assertEquals("cookie_bar", cookie.getValue());

        ForestResponse<String> response = cookieClient.testCookieWithCallback((request, cookies) -> {
            cookies.addCookie(cookie);
        });
        assertNotNull(response);
        String result = response.getResult();
        assertEquals(CookieMockServer.EXPECTED, result);
    }

    @Test
    public void testCookieWithInterceptor() {
        cookieClient.testLoginWithInterceptor();
        ForestResponse<String> response = cookieClient.testCookieWithInterceptor();
        assertNotNull(response);
        String result = response.getResult();
        assertEquals(CookieMockServer.EXPECTED, result);
    }


}
