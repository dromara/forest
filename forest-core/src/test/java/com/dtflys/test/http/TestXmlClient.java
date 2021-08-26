package com.dtflys.test.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.json.ForestFastjsonConverter;
import com.dtflys.test.converter.entity.XmlEntity;
import com.dtflys.test.converter.entity.XmlOrder;
import com.dtflys.test.http.client.XmlClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestXmlClient extends BaseClientTest {

    public final static String EXPECTED = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "\n" +
            "<Service version=\"1.0\">\n" +
            "  <ResultCode>0</ResultCode>\n" +
            "  <ResultMsg>Success</ResultMsg>\n" +
            "  <Total>133</Total>\n" +
            "  <Order>\n" +
            "    <PaymentRefundDate>2020-08-02 11:05:33</PaymentRefundDate>\n" +
            "    <PaymentDate>2020-08-02 11:05:33</PaymentDate>\n" +
            "    <Status>1</Status>\n" +
            "    <OrderNo>100000</OrderNo>\n" +
            "    <CartNo>66666666</CartNo>\n" +
            "    <BuyerPaidAmount>2199.0000</BuyerPaidAmount>\n" +
            "    <ServiceFee>264.0000</ServiceFee>\n" +
            "    <SettledDate>2020-08-26 12:00:00</SettledDate>\n" +
            "    <SettledAmount>1935.0000</SettledAmount>\n" +
            "  </Order>\n" +
            "</Service>\n";

    public MockWebServer server = new MockWebServer();

    private XmlClient xmlClient;

    private static ForestConfiguration configuration;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    public TestXmlClient(HttpBackend backend) {
        super(backend, configuration);
        configuration.setVariableValue("port", server.getPort());
        ForestFastjsonConverter fastjsonConverter = new ForestFastjsonConverter();
        fastjsonConverter.setSerializerFeature(SerializerFeature.SortField);
        configuration.setJsonConverter(fastjsonConverter);
        xmlClient = configuration.createInstance(XmlClient.class);
    }

    @Test
    public void testGetXmlData() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlEntity<XmlOrder> data = xmlClient.getXmlData();
        System.out.println(JSON.toJSONString(data));
        assertThat(data)
                .isNotNull()
                .extracting(XmlEntity::getResultCode, XmlEntity::getResultMsg)
                .contains(0, "Success");
        assertThat(data.getOrder())
                .isNotNull()
                .extracting(List::isEmpty)
                .isEqualTo(false);
        assertThat(data.getOrder().get(0))
                .extracting(XmlOrder::getBuyerPaidAmount)
                .isEqualTo(2199.0);
    }


}
