package com.dtflys.forest.test;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.logging.ForestSlf4jLogger;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class TestLog {

    public final static String EXPECTED = "{\"status\":\"1\", \"data\":\"2\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    private ForestConfiguration configuration;

    @Before
    public void setUp() {
        this.configuration = ForestConfiguration.createConfiguration();
    }

    @Test
    public void testRequestLogConfiguration() {
        ForestRequest request = configuration.request();
        assertThat(request.getLogConfiguration()).isNotNull();

        request = configuration.request();
        request.setLogConfiguration(null);
        assertThat(request.getLogConfiguration()).isNotNull();
    }

    @Test
    public void testLogEnabled() {
        configuration.setLogEnabled(false);
        ForestRequest request = configuration.request();
        assertThat(request.isLogEnabled()).isEqualTo(false);

        request.setLogConfiguration(null);
        assertThat(request.isLogEnabled()).isEqualTo(false);

        request.setLogConfiguration(null);
        request.logEnabled(true);
        assertThat(request.isLogEnabled()).isEqualTo(true);
    }

    @Test
    public void testLogRequest() {
        ForestRequest request = configuration.request();
        assertThat(request.isLogRequest()).isEqualTo(configuration.isLogRequest());

        request.setLogConfiguration(null);
        assertThat(request.isLogRequest()).isEqualTo(configuration.isLogRequest());

        request.setLogConfiguration(null);
        request.logRequest(!configuration.isLogRequest());
        assertThat(request.isLogRequest()).isEqualTo(!configuration.isLogRequest());
    }

    @Test
    public void testLogResponseStatus() {
        ForestRequest request = configuration.request();
        assertThat(request.isLogResponseStatus()).isEqualTo(configuration.isLogResponseStatus());

        request.setLogConfiguration(null);
        assertThat(request.isLogResponseStatus()).isEqualTo(configuration.isLogResponseStatus());

        request.setLogConfiguration(null);
        request.logResponseStatus(!configuration.isLogRequest());
        assertThat(request.isLogResponseStatus()).isEqualTo(!configuration.isLogResponseStatus());
    }

    @Test
    public void testLogRequestContent() {
        ForestLogger logger = Mockito.spy(new ForestSlf4jLogger(TestLog.class));
        DefaultLogHandler logHandler = new DefaultLogHandler(logger);

        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .setResponseCode(200));
        ForestResponse response = configuration.get("/test")
                .host(server.getHostName())
                .port(server.getPort())
                .logHandler(logHandler)
                .executeAsResponse();

        verify(logger).info(eq("[Forest] Request (httpclient): \n" +
                "\tGET http://" + server.getHostName() + ":" + server.getPort() + "/test HTTP\n" +
                "\tHeaders: \n" +
                "\t\tUser-Agent: forest/dev"));
        verify(logger).info(eq("[Forest] Response: Status = 200, Time = " + response.getTimeAsMillisecond() + "ms"));

    }


    @Test
    public void testLogResponseHeadersContent() {
        ForestLogger logger = Mockito.spy(new ForestSlf4jLogger(TestLog.class));
        DefaultLogHandler logHandler = new DefaultLogHandler(logger);

        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setHeader("Server", "MockWebServer")
                .setBody(EXPECTED)
                .setResponseCode(200));
        ForestResponse response = configuration.get("/test")
                .host(server.getHostName())
                .port(server.getPort())
                .logHandler(logHandler)
                .logRequest(false)
                .logResponseHeaders(true)
                .executeAsResponse();

        verify(logger).info(eq("[Forest] Response: \n" +
                "\tResponse Status:\n" +
                "\t\tStatus = 200, Time = " + response.getTimeAsMillisecond() + "ms\n" +
                "\tResponse Headers:\n" +
                "\t\tContent-Type: application/json\n" +
                "\t\tServer: MockWebServer\n" +
                "\t\tContent-Length: 26"));
    }


    @Test
    public void testLogResponseContent() {
        ForestLogger logger = Mockito.spy(new ForestSlf4jLogger(TestLog.class));
        DefaultLogHandler logHandler = new DefaultLogHandler(logger);

        server.enqueue(new MockResponse()
                .setBody(EXPECTED)
                .setResponseCode(200));
        configuration.get("/test")
                .host(server.getHostName())
                .port(server.getPort())
                .logHandler(logHandler)
                .logRequest(false)
                .logResponseStatus(false)
                .logResponseContent(true)
                .executeAsString();

        verify(logger).info(eq("[Forest] Response Content:\n" +
                "\t" + EXPECTED));
    }


}
