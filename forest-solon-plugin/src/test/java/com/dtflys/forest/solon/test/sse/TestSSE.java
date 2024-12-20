package com.dtflys.forest.solon.test.sse;

import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.http.ForestSSE;
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
@SolonTest(env = "sse")
public class TestSSE {

    @Rule
    public MockWebServer server = new MockWebServer();

    @Inject
    private MySSEClient sseClient;
    
    @BindingVar("port")
    public Integer getPort() {
        return server.getPort();
    }
    
    @Test
    public void testSSE() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n"
        ));
        
        StringBuilder builder = new StringBuilder();

        sseClient.testSSE()
                .addOnData((eventSource, name, value) -> {
                    builder.append("Receive ").append(name).append(": ").append(value).append("\n");
                })
                .listen();
        
        assertThat(builder.toString()).isEqualTo(
                "Receive data: start\n" +
                "Receive data: hello\n"
        );
    }


    @Test
    public void testSSE_withInterceptor() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:hello\n"
        ));

        ForestSSE sse = sseClient.testSSE_withInterceptor().listen();

        assertThat(sse.getRequest().getAttachment("text").toString()).isEqualTo(
                "onSuccess\n" +
                "afterExecute\n" +
                "Receive name=data; value=start; comp=test\n" +
                "Receive name=data; value=hello; comp=test\n"
        );
    }

}
