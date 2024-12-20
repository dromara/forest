package com.dtflys.forest.test.http.sse;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestSSE;
import com.dtflys.forest.test.model.Contact;
import com.dtflys.forest.test.model.TestUser;
import com.dtflys.forest.test.sse.MySSEHandler;
import com.dtflys.forest.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestSSEClient extends BaseClientTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    private SSEClient sseClient;

    private static ForestConfiguration configuration = ForestConfiguration.createConfiguration();

    public TestSSEClient(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", server.getPort());
        sseClient = configuration.client(SSEClient.class);
    }

    @Test
    public void testSSE() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:{\"event\": \"message\", \"conversation_id\": \"aee49897-5214308b6b2d\", \"message_id\": \"9e292a7d\", \"created_at\": 1734689225 \"answer\": \"I\", \"from_variable_selector\": null}\n" +
                "event:{\"name\":\"Peter\",\"age\": \"18\",\"phone\":\"12345678\"}\n" +
                "event:close\n" +
                "data:dont show"
        ));

        StringBuffer buffer = new StringBuffer();

        sseClient.testSSE()
            .setOnOpen(eventSource -> {
                buffer.append("SSE Open\n");
            }).setOnClose((req, res) -> {
                buffer.append("SSE Close");
            }).addOnData((eventSource, name, value) -> {
                buffer.append("Receive " + name + ": " + value + "\n");
            }).addOnEvent((eventSource, name, value) -> {
                buffer.append("Receive " + name + ": " + value + "\n");
                if ("close".equals(value)) {
                    eventSource.close();
                }
            }).addOnEvent(Contact.class, (eventSource, name, value) -> {
                buffer.append("name: " + value.getName() + "; age: " + value.getAge() + "\n");
            })
        .listen();

        System.out.println(buffer);
        assertThat(buffer.toString()).isEqualTo(
                "SSE Open\n" +
                "Receive data: start\n" +
                "Receive data: {\"event\": \"message\", \"conversation_id\": \"aee49897-5214308b6b2d\", \"message_id\": \"9e292a7d\", \"created_at\": 1734689225 \"answer\": \"I\", \"from_variable_selector\": null}\n" +
                "Receive event: {\"name\":\"Peter\",\"age\": \"18\",\"phone\":\"12345678\"}\n" +
                "name: Peter; age: 18\n" +
                "Receive event: close\n" +
                "SSE Close"
        );
    }

    @Test
    public void testSSE_withCustomClass() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:{\"event\": \"message\", \"conversation_id\": \"aee49897-5214308b6b2d\", \"message_id\": \"9e292a7d\", \"created_at\": 1734689225 \"answer\": \"I\", \"from_variable_selector\": null}\n" +
                "event:{\"name\":\"Peter\",\"age\": \"18\",\"phone\":\"12345678\"}\n" +
                "event:close\n" +
                "data:dont show"
        ));

        MySSEHandler sse = sseClient.testSSE_withCustomClass().listen();
        
        System.out.println(sse.getStringBuffer());
        assertThat(sse.getStringBuffer().toString()).isEqualTo(
                "SSE Open\n" +
                "Receive data: start\n" +
                "Receive data: {\"event\": \"message\", \"conversation_id\": \"aee49897-5214308b6b2d\", \"message_id\": \"9e292a7d\", \"created_at\": 1734689225 \"answer\": \"I\", \"from_variable_selector\": null}\n" +
                "name: Peter; age: 18; phone: 12345678\n" +
                "receive close --- close\n" +
                "SSE Close"
        );
    }

    @Test
    public void testSSE_withInterceptor() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(
                "data:start\n" +
                "data:{\"event\": \"message\", \"conversation_id\": \"aee49897-5214308b6b2d\", \"message_id\": \"9e292a7d\", \"created_at\": 1734689225 \"answer\": \"I\", \"from_variable_selector\": null}\n" +
                "event:{\"name\":\"Peter\",\"age\": \"18\",\"phone\":\"12345678\"}\n" +
                "event:close\n" +
                "data:dont show"
        ));

        ForestSSE sse = sseClient.testSSE_withInterceptor().listen();

        System.out.println(sse.getRequest().getAttachment("text"));
        assertThat(sse.getRequest().getAttachment("text").toString()).isEqualTo(
                "MySSEInterceptor onSuccess\n" +
                "MySSEInterceptor afterExecute\n" +
                "MySSEInterceptor onSSEOpen\n" +
                "Receive data: start\n" +
                "Receive data: {\"event\": \"message\", \"conversation_id\": \"aee49897-5214308b6b2d\", \"message_id\": \"9e292a7d\", \"created_at\": 1734689225 \"answer\": \"I\", \"from_variable_selector\": null}\n" +
                "name: Peter; age: 18; phone: 12345678\n" +
                "receive close --- close\n" +
                "MySSEInterceptor onSSEClose"
        );
    }


}
