package com.dtflys.test;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestGenericForestClient {

    public final static String EXPECTED = "{\"status\":\"ok\"}";

    @Rule
    public final MockWebServer server = new MockWebServer();


    @Test
    public void testRequest() {
        assertThat(Forest.config().request()).isNotNull();
    }


    @Test
    public void testRequestExecute() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(
                Forest.get("http://localhost:" + server.getPort())
                        .execute())
                .isNotNull()
                .isInstanceOf(ForestResponse.class);
    }


}
