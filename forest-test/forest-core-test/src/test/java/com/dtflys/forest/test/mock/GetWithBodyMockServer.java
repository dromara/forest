package com.dtflys.forest.test.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class GetWithBodyMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";
    
    private MockServerClient clientAndServer;

    public GetWithBodyMockServer(Object target) {
        super(target);
    }

    public void initServer() {
        if (clientAndServer != null) {
            return;
        }

        clientAndServer = new MockServerClient("localhost", getPort());
        clientAndServer.when(
                request()
                        .withPath("/hello/user")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plain"))
                        .withQueryStringParameter("param", "1")
                        .withBody("username=foo&password=123456")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );
    }

}
