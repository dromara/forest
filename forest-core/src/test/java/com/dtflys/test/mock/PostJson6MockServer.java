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
public class PostJson6MockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static Integer port = 5019;

    public PostJson6MockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/json")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8"))
                        .withBody("{\"name\":\"test\",\"data\":[\"A\",\"B\",\"C\"]}")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );


        mockClient.when(
                request()
                        .withPath("/json-date")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.CONTENT_TYPE, "application/json"))
                        .withBody("{\"createTime\":\"2020-10-11 10:12:00\"}")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );

    }

}
