package com.dtflys.forest.test.convert.xml.jaxb;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.test.convert.xml.jaxb.client.XmlEncoderClient;
import com.dtflys.forest.utils.ForestDataType;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestXmlEncoder {

    public final static String EXPECTED = "{\"status\":\"ok\"}";


    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final XmlEncoderClient xmlEncoderClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestXmlEncoder() {
        configuration.variable("port", server.getPort());
        this.xmlEncoderClient = configuration.client(XmlEncoderClient.class);
    }


    @Test
    public void testEncoder() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlEncoderClient.Entry entry = new XmlEncoderClient.Entry("AAA", "BBB");
        ForestRequest request = xmlEncoderClient.testEncoder("xml", entry);
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

    }
}
