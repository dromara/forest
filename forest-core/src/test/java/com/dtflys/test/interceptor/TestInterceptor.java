package com.dtflys.test.interceptor;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.test.http.BaseClientTest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.TestGetClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-18 16:57
 */
public class TestInterceptor extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetClient.class);

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private static InterceptorClient interceptorClient;

    private static BaseInterceptorClient baseInterceptorClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
        configuration.setCacheEnabled(false);
    }

    @Override
    public void afterRequests() {
    }

    public TestInterceptor(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVar("port", server.getPort());
        interceptorClient = configuration.createInstance(InterceptorClient.class);
        baseInterceptorClient = configuration.createInstance(BaseInterceptorClient.class);
    }


    @Test
    public void testSimpleInterceptor() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        configuration.getLogHandler().setLogger(logger);
        assertThat(interceptorClient.simple())
            .isNotNull()
            .isEqualTo("XX: " + EXPECTED);
        Mockito.verify(logger).info("[Forest] Request (" + configuration.getBackend().getName() + "): \n" +
                "\t[Type Change]: POST -> GET\n" +
                "\tGET http://localhost:" + server.getPort() + "/hello/user?username=foo&username=foo HTTP\n" +
                "\tHeaders: \n" +
                "\t\tUser-Agent: " + ForestHeader.DEFAULT_USER_AGENT_VALUE + "\n" +
                "\t\tAccept: text/plain");
    }

    @Test
    public void testMultipleInterceptor() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(interceptorClient.multiple())
                .isNotNull()
                .isEqualTo("YY: XX: " + EXPECTED);
    }

    @Test
    public void testCutoffInterceptor() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(interceptorClient.beforeCutoff("a")).isNull();
    }


    @Test
    public void testBaseNoneInterceptor() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseInterceptorClient.none())
                .isNotNull()
                .isEqualTo("Base: " + EXPECTED);
    }

    @Test
    public void testBaseSimpleInterceptor() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        try {
            assertThat(baseInterceptorClient.simple("xxx"))
                    .isNotNull()
                    .isEqualTo("XX: Base: " + EXPECTED);
        } catch (Throwable th) {
            assertThat(configuration.getBackend().getName()).isEqualTo("httpclient");
        }

    }


    @Test
    public void testBaseSimpleInterceptorWithGenerationType() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(baseInterceptorClient.generationType())
                .isNotNull()
                .extracting(ForestResponse::result)
                .isEqualTo("XX: Base: " + EXPECTED);
    }


}
