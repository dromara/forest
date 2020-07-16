package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.mock.PostMockServer;
import com.dtflys.test.mock.PostXmlMockServer;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.backend.okhttp3.OkHttp3Backend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.PostClient;
import com.dtflys.test.http.model.XmlTestParam;
import com.dtflys.test.mock.PostMockServer;
import com.dtflys.test.mock.PostXmlMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-11 16:55
 */
public class TestPostXmlClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestPostXmlClient.class);

    @Rule
    public PostXmlMockServer server = new PostXmlMockServer(this);

    private static ForestConfiguration configuration;

    private static PostClient postClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", PostXmlMockServer.port);
    }

    public TestPostXmlClient(HttpBackend backend) {
        super(backend, configuration);
        postClient = configuration.createInstance(PostClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testXmlPost() {
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        String result = postClient.postXml(testParam);
        log.info("response: " + result);
        assertNotNull(result);
        Assert.assertEquals(PostMockServer.EXPECTED, result);
    }


    @Test
    public void testXmlPost2() {
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        String result = postClient.postXml2(testParam);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }


    @Test
    public void testXmlPost3() {
        XmlTestParam testParam = new XmlTestParam();
        testParam.setA(1);
        testParam.setB(2);
        String result = postClient.postXml3(testParam);
        log.info("response: " + result);
        assertNotNull(result);
        assertEquals(PostMockServer.EXPECTED, result);
    }


}
