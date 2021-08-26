package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.retryer.BackOffRetryer;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.ErrorMockServer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 15:02
 */
public class TestErrorClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"error\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }


    public TestErrorClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        getClient = configuration.createInstance(GetClient.class);
    }

    @Override
    public void afterRequests() {
    }

    @Test
    public void testErrorGet() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody(EXPECTED));
        AtomicReference<String> content = new AtomicReference<>(null);
        assertThat(getClient.errorGet((ex, request, response) -> {
            content.set(response.getContent());
            assertThat(response)
                    .isNotNull()
                    .extracting(
                            ForestResponse::isError,
                            ForestResponse::getStatusCode,
                            ForestResponse::getContent)
                    .contains(true, 404, EXPECTED);
            response.setResult("onError=true");
        }))
            .isNotNull()
            .isEqualTo("onError=true");
        assertThat(content.get())
                .isNotNull()
                .isEqualTo(EXPECTED);
    }

    @Test
    public void testErrorGet2() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody(EXPECTED));
        assertThat(getClient.errorGet2())
            .isNotNull()
            .extracting(
                    ForestResponse::isError,
                    ForestResponse::getStatusCode,
                    ForestResponse::getContent)
            .contains(true, 400, EXPECTED);
    }

    @Test
    public void testErrorGet3() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody(EXPECTED));
        boolean hasError = false;
        try {
            getClient.errorGet3();
        } catch (ForestNetworkException ex) {
            hasError = true;
            assertThat(ex.getStatusCode()).isEqualTo(500);
            assertThat(ex.getResponse())
                    .isNotNull()
                    .extracting(ForestResponse::getContent)
                    .isEqualTo(EXPECTED);
        }
        assertThat(hasError).isTrue();
    }

    @Test
    public void testErrorGet4() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody(EXPECTED));
        assertThat(getClient.errorGet4())
                .isNotNull()
                .extracting(
                        ForestResponse::getStatusCode,
                        ForestResponse::getResult)
                .contains(500, "{\"error\": true, \"interceptor\": true}");
    }



    @Test
    public void testErrorGetWithRetry() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody(EXPECTED));
        AtomicReference<BackOffRetryer> retryerAtomicReference = new AtomicReference<>(null);
        getClient.errorGetWithRetry((ex, request, response) -> {
            retryerAtomicReference.set((BackOffRetryer) request.getRetryer());
        });
        BackOffRetryer retryer = retryerAtomicReference.get();
        assertThat(retryer).isNotNull();
        assertThat(retryer.getMaxRetryCount()).isEqualTo(3);
        assertThat(retryer.getMaxRetryInterval()).isEqualTo(2000);
        assertThat(retryer.getWaitedTime()).isEqualTo(1000 + 2000 + 2000);
    }

    @Test
    public void testErrorGetWithRetry2() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody(EXPECTED));
        boolean hasThrow = false;
        try {
            getClient.errorGetWithRetry();
        } catch (Throwable th) {
            hasThrow = true;
        }
        assertThat(hasThrow).isTrue();
    }


}
