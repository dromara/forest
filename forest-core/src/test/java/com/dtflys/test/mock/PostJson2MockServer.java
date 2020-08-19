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
public class PostJson2MockServer extends MockServerRule {

//    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static String EXPECTED = "该IP没有权限访问";


    public final static Integer port = 5015;

    public PostJson2MockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/json")
                        .withMethod("POST")
                        .withHeader(new Header(HttpHeaders.CONTENT_TYPE, "application/json"))
                        .withBody("{\"username\":\"foo\"}")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );
    }

}
