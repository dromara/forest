package com.dtflys.forest.solon.test.when;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(value = TestWhen.class, args = "-env=when")
public class TestWhen {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private MyWhenCondition myWhenCondition;

    @Inject
    private SpringWhenClient springWhenClient;

    @Test
    public void testSuccessWhen() {
        myWhenCondition.setSuccessInvokeCount(0);
        myWhenCondition.setRejectStatusCode(203);
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicBoolean isError = new AtomicBoolean(false);
        springWhenClient.testSuccessWhen(server.getPort(), (ex, req, res) -> {
            isError.set(true);
        });
        assertThat(isError.get()).isTrue();
        assertThat(myWhenCondition.getSuccessInvokeCount()).isEqualTo(1);
    }

    @Test
    public void testRetryWhen() {
        myWhenCondition.setRetryInvokeCount(0);
        myWhenCondition.setRetryStatusCode(203);
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        server.enqueue(new MockResponse().setBody(EXPECTED).setResponseCode(203));
        AtomicBoolean isSuccess = new AtomicBoolean(false);
        String result = springWhenClient.testRetryWhen(server.getPort(), (data, req, res) -> {
            assertThat(req.getCurrentRetryCount()).isEqualTo(3);
            isSuccess.set(true);
        });
        assertThat(result).isNotNull().isEqualTo(EXPECTED);
        assertThat(myWhenCondition.getRetryInvokeCount()).isEqualTo(4);
        assertThat(isSuccess.get()).isTrue();
    }

}
