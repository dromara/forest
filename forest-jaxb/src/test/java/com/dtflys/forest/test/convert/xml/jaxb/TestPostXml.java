package com.dtflys.forest.test.convert.xml.jaxb;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.test.convert.xml.jaxb.client.PostXmlClient;
import com.dtflys.forest.test.convert.xml.jaxb.pojo.XmlTestParam;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static com.dtflys.forest.mock.MockServerRequest.mockRequest;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPostXml {

    public final static String EXPECTED = "{\"status\":\"ok\"}";


    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final PostXmlClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestPostXml() {
        configuration.variable("port", server.getPort());
        postClient = configuration.createInstance(PostXmlClient.class);
    }

    @Test
    public void testPostXml() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXml(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlInDataProperty() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlInDataProperty(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlInDataProperty2() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlInDataProperty2(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlWithXMLBodyAnn() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlWithXMLBodyAnn(testParam))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

    @Test
    public void testPostXmlBodyString() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        assertThat(postClient.postXmlBodyString(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n"))
                .isNotNull()
                .isEqualTo(EXPECTED);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<misc>\n" +
                                "    <a>1</a>\n" +
                                "    <b>2</b>\n" +
                                "</misc>\n");
    }


    @Test
    public void testPostXmlWithXMLBodyAnnAndReturnObj() {
        server.enqueue(new MockResponse()
                .setBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>3</a>\n" +
                        "    <b>4</b>\n" +
                        "</misc>\n"));
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        assertThat(postClient.postXmlWithXMLBodyAnnAndReturnObj(testParam))
                .isNotNull()
                .extracting(XmlTestParam::getA, XmlTestParam::getB)
                .contains(10, 20);
        mockRequest(server)
                .assertMethodEquals("POST")
                .assertPathEquals("/xml-response")
                .assertHeaderEquals("Content-Type", "application/xml")
                .assertBodyEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<misc>\n" +
                        "    <a>1</a>\n" +
                        "    <b>2</b>\n" +
                        "</misc>\n");
    }

}
