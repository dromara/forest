package org.dromara.forest.solon.test.array;

import org.dromara.forest.annotation.BindingVar;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "array")
public class TestArrayClient {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private ArrayClient arrayClient;

    @BindingVar("port") //需要在类上加 @Component
    public int port() {
        return server.getPort();
    }

    @Test
    public void testArrayFromProperties() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = arrayClient.arrayQueryFromProperties();
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
    }


}
