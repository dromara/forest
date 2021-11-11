package com.dtflys.test.http.encoder;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.test.http.BaseClientTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestEncoderClient extends BaseClientTest {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    @Rule
    public MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private EncoderClient encoderClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestEncoderClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        encoderClient = configuration.client(EncoderClient.class);
    }


    @Test
    public void testEncoder() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = encoderClient.testEncoder("AAA", "BBB");
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.JSON);
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("{\"name\":\"AAA\",\"value\":\"BBB\"}");
    }

    @Test
    public void testEncoder2() {
        EncoderClient.Entry entry = new EncoderClient.Entry("AAA", "BBB");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = encoderClient.testEncoder2("json", entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.JSON);
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("{\"name\":\"AAA\",\"value\":\"BBB\"}");

        server.enqueue(new MockResponse().setBody(EXPECTED));
        request = encoderClient.testEncoder2("xml", entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.XML);
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<entry>\n" +
                        "    <name>AAA</name>\n" +
                        "    <value>BBB</value>\n" +
                        "</entry>\n");

        server.enqueue(new MockResponse().setBody(EXPECTED));
        request = encoderClient.testEncoder2("form", entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.FORM);
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("name=AAA&value=BBB");

        server.enqueue(new MockResponse().setBody(EXPECTED));
        request = encoderClient.testEncoder2("text", entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.TEXT);
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("Entry{name='AAA', value='BBB'}");


        server.enqueue(new MockResponse().setBody(EXPECTED));
        request = encoderClient.testEncoder2("binary", entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.BINARY);
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("Entry{name='AAA', value='BBB'}".getBytes(StandardCharsets.UTF_8));

    }

    @Test
    public void testMultipart() {
        EncoderClient.Entry entry = new EncoderClient.Entry("AAA", "BBB");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = encoderClient.testMutlipart(entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.MULTIPART);
        request.execute();
    }


    @Test
    public void testEncoder3() {
        EncoderClient.Entry entry = new EncoderClient.Entry("AAA", "BBB");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = encoderClient.testEncoder3(entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.JSON);
        assertThat(request.getEncoder()).isNotNull().isInstanceOf(MyEncoder.class);
        request.execute();
        mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals("Data: " + entry);
    }

    @Test
    public void testFastjson() {
        EncoderClient.Entry entry = new EncoderClient.Entry("AAA", "BBB");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = encoderClient.testFastjson(entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.JSON);
        assertThat(request.getEncoder()).isNotNull().isInstanceOf(ForestFastjsonConverter.class);
        request.execute();
        mockRequest(server)
                .assertBodyEquals("{\"name\":\"AAA\",\"value\":\"BBB\"}");
    }

    @Test
    public void testJackson() {
        EncoderClient.Entry entry = new EncoderClient.Entry("AAA", "BBB");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = encoderClient.testJackson(entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.JSON);
        assertThat(request.getEncoder()).isNotNull().isInstanceOf(ForestJacksonConverter.class);
        request.execute();
        mockRequest(server)
                .assertBodyEquals("{\"name\":\"AAA\",\"value\":\"BBB\"}");
    }

    @Test
    public void testGson() {
        EncoderClient.Entry entry = new EncoderClient.Entry("AAA", "BBB");
        server.enqueue(new MockResponse().setBody(EXPECTED));
        ForestRequest request = encoderClient.testGson(entry);
        assertThat(request).isNotNull();
        assertThat(request.bodyType()).isNotNull().isEqualTo(ForestDataType.JSON);
        assertThat(request.getEncoder()).isNotNull().isInstanceOf(ForestGsonConverter.class);
        request.execute();
        mockRequest(server)
                .assertBodyEquals("{\"name\":\"AAA\",\"value\":\"BBB\"}");
    }

}
