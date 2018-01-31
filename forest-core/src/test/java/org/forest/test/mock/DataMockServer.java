package org.forest.test.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class DataMockServer extends MockServerRule {


    public final static String DATA_RESULT = "{\"status\": 1, \"data\": {\"list\": [1, 2, 3, 4, 5, 10, 32], \"map\": {\"a\": 1, \"b\": 2}}}";

    public DataMockServer(Object target) {
        super(target, 3000);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 3000);

        mockClient.when(
                request()
                        .withPath("/hello/data")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withQueryStringParameter("type",  "data")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(DATA_RESULT)
        );

    }

}
