package org.forest.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class AsyncGetMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";


    public AsyncGetMockServer(Object target) {
        super(target, 5000);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", 5000);
        mockClient.when(
                request()
                        .withPath("/hello/user")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plan"))
                        .withQueryStringParameter("username",  "foo")
        )
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
                        .withDelay(TimeUnit.SECONDS, 1)
        );

    }

}
