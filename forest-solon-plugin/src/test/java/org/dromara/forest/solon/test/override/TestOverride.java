package org.dromara.forest.solon.test.override;

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
@SolonTest(env = "test2")
public class TestOverride {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private MyClient myClient;

    @Inject
    private MyClientImpl myClientImpl;

    @BindingVar("port")
    public int getPort() {
        return server.getPort();
    }

    @Test
    public void test1() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        String result = myClient.test1();
        assertThat(result).isNotNull().isEqualTo("Global: " + EXPECTED);
    }

    @Test
    public void testOverride() {
        String result = myClientImpl.test1();
        assertThat(result).isEqualTo("xxxx");
    }


}
