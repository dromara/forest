package org.dromara.forest.test.convert.xml.jaxb;

import org.dromara.forest.backend.ContentType;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.test.convert.xml.jaxb.client.XmlEncoderClient;
import org.dromara.forest.utils.ForestDataType;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.dromara.forest.mock.MockServerRequest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

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
        configuration.setVariableValue("port", server.getPort());
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
        MockServerRequest.mockRequest(server)
                .assertHeaderEquals("Content-Type", ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
                .assertBodyEquals(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<entry>\n" +
                                "    <name>AAA</name>\n" +
                                "    <value>BBB</value>\n" +
                                "</entry>\n");

    }
}
