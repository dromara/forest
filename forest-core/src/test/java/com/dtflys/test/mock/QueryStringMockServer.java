package com.dtflys.test.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-09-03 11:43
 */
public class QueryStringMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static Integer port = 5032;

    public QueryStringMockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/hello/user")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plain"))
                        .withQueryStringParameter(new Parameter("foo"))
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(EXPECTED)
                );

    }

}
