package org.forest.test.mock;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class PostXmlMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public PostXmlMockServer(Object target) {
        super(target, 5000);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/xml")
                        .withMethod("POST")
                        .withBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                "<misc>\n" +
                                "    <a>1</a>\n" +
                                "    <b>2</b>\n" +
                                "</misc>\n")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );
    }

}
