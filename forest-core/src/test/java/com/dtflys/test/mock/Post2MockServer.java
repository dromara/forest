package com.dtflys.test.mock;

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
public class Post2MockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static Integer port = 5008;

    public Post2MockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/form-array")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plain"))
                        .withBody("username=foo&password=123456&idList=1%2C2%2C3&cause%5B0%5D.id=1&cause%5B0%5D.score=87&cause%5B1%5D.id=2&cause%5B1%5D.score=73")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );
    }

}
