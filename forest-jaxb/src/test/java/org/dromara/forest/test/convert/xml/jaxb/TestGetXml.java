package org.dromara.forest.test.convert.xml.jaxb;

import com.alibaba.fastjson.JSON;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.test.convert.xml.jaxb.client.GetXmlClient;
import org.dromara.forest.test.convert.xml.jaxb.pojo.XmlEntity;
import org.dromara.forest.test.convert.xml.jaxb.pojo.XmlOrder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestGetXml {

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


    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final GetXmlClient getXmlClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestGetXml() {
        configuration.setVariableValue("port", server.getPort());
        getXmlClient = configuration.client(GetXmlClient.class);
    }

    @Test
    public void testGetXmlData() {
        server.enqueue(new MockResponse().setBody(EXPECTED));
        XmlEntity<XmlOrder> data = getXmlClient.getXmlData();
        System.out.println(JSON.toJSONString(data));
        AssertionsForClassTypes.assertThat(data)
                .isNotNull()
                .extracting(XmlEntity::getResultCode, XmlEntity::getResultMsg)
                .contains(0, "Success");
        AssertionsForClassTypes.assertThat(data.getOrder())
                .isNotNull()
                .extracting(List::isEmpty)
                .isEqualTo(false);
        AssertionsForClassTypes.assertThat(data.getOrder().get(0))
                .extracting(XmlOrder::getBuyerPaidAmount)
                .isEqualTo(2199.0);
    }

}
