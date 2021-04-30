package com.dtflys.test.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;
import org.mockserver.model.Parameter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class UrlEncodedMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static Integer port = 5082;

    public UrlEncodedMockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        MockServerClient mockClient = new MockServerClient("localhost", port);
        mockClient.when(
                request()
                        .withPath("/encoded")
                        .withMethod("GET")
                        .withQueryStringParameter("lang", "中文")
                        .withQueryStringParameter("code", "AbcD12#$iTXI")
                        .withQueryStringParameter("data", "il&felUFO3o=P")
                        .withQueryStringParameter("content", "中文内容")
        )
        .respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );

        mockClient.when(
                request()
                        .withPath("/encoded")
                        .withMethod("POST")
                        .withQueryStringParameter("lang",  "中文")
                        .withQueryStringParameter("code",  "AbcD12#$iTXI")
                        .withQueryStringParameter("data",  "il&felUFO3o=P")
                        .withQueryStringParameter("content",  "中文内容")
        )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(EXPECTED)
                );

    }

}
