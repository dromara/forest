package org.forest.test.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-03-01 19:17
 */
public class SSLMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";


    public SSLMockServer(Object target) {
        super(target, 5000);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient
        .when(
                request()
                        .withPath("/hello/user")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withQueryStringParameter("username",  "foo")
                        .withSecure(true)
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(EXPECTED)
                );

    }

}
