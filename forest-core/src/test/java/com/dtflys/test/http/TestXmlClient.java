package com.dtflys.test.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.test.converter.entity.XmlEntity;
import com.dtflys.test.converter.entity.XmlOrder;
import com.dtflys.test.http.client.XmlClient;
import com.dtflys.test.mock.GenericXmlMockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestXmlClient extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestSSLClient.class);

    @Rule
    public GenericXmlMockServer server = new GenericXmlMockServer(this);

    private XmlClient xmlClient;


    private static ForestConfiguration configuration;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", GenericXmlMockServer.port);
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setSerializerFeature(SerializerFeature.SortField);
        configuration.setJsonConverter(fastjsonConverter);
    }

    public TestXmlClient(HttpBackend backend) {
        super(backend, configuration);
        xmlClient = configuration.createInstance(XmlClient.class);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }


    @Test
    public void testGetXmlData() {
        XmlEntity<XmlOrder> data = xmlClient.getXmlData();
        System.out.println(JSON.toJSONString(data));
        assertEquals(0, data.getResultCode());
        assertEquals("Success", data.getResultMsg());
        assertTrue(!data.getOrder().isEmpty());
        XmlOrder order = data.getOrder().get(0);
        assertEquals(Double.valueOf(2199.0F), order.getBuyerPaidAmount());
    }


}
