package com.dtflys.test.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class GenericXmlMockServer extends MockServerRule {

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

    public final static Integer port = 5092;

    public GenericXmlMockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/test/xml")
                        .withMethod("GET")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(EXPECTED)
                );

    }

}
