package com.dtflys.forest.test.http.variable;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestVariableUndefinedException;
import com.dtflys.forest.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestVarClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private VarClient varClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestVarClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        varClient = configuration.client(VarClient.class);
    }

    @Test
    public void testNullValueVar() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Throwable exception = null;
        try {
            varClient.nullValueVar();
        } catch (Throwable th) {
            exception = th;
        }
        assertThat(exception)
                .isNotNull()
                .isInstanceOf(ForestVariableUndefinedException.class);
    }
}
