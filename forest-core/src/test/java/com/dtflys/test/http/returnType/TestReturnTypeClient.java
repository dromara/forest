package com.dtflys.test.http.returnType;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.utils.TypeReference;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestReturnTypeClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private ReturnTypeClient returnTypeClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }


    public TestReturnTypeClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        returnTypeClient = configuration.client(ReturnTypeClient.class);
    }


    private static class Res {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


    @Test
    public void testGetGenericClass() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = returnTypeClient.getGenericClass(String.class);
        assertThat(result).isNotNull().isEqualTo(EXPECTED);

        server.enqueue(new MockResponse().setBody(EXPECTED));
        Res res = returnTypeClient.getGenericClass(Res.class);
        assertThat(res).isNotNull();
        assertThat(res.getStatus()).isEqualTo("ok");
    }

    @Test
    public void testGetGenericType() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Res res = returnTypeClient.getGenericType(new TypeReference<Res>() {});
        assertThat(res).isNotNull();
        assertThat(res.getStatus()).isEqualTo("ok");
    }

    @Test
    public void testGetGenericTypeReference() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        Res res = returnTypeClient.getGenericTypeReference(new TypeReference<Res>() {});
        assertThat(res).isNotNull();
        assertThat(res.getStatus()).isEqualTo("ok");
    }

}
