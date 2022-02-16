package com.dtflys.test.mock;

import org.apache.http.HttpHeaders;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class GetMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\":\"ok\"}";


    public GetMockServer(Object target) {
        super(target);
    }

    public void initServer() {
        ClientAndServer clientAndServer = new ClientAndServer("localhost", getPort());
        clientAndServer.when(
                request()
                        .withPath("/hello/user")
                        .withMethod("GET")
                        .withHeader(new Header(HttpHeaders.ACCEPT, "text/plain"))
                        .withQueryStringParameter("username",  "foo")
        )
        .respond(
                response()
                        .withStatusCode(200)
                        .withHeaders(
                                new Header("Content-Type", "application/vnd.kafka.v2+json"),
                                new Header("Vary", "Accept-Encoding, User-Agent")
                        )
                        .withBody(EXPECTED)
        );

        clientAndServer.when(
                        request()
                                .withPath("/con")
                                .withMethod("GET")
                                .withHeader(new Header(HttpHeaders.ACCEPT, "text/plain"))
                                .withQueryStringParameter("username",  "foo")
                )
                .respond(
                        httpRequest -> {
                            String id = httpRequest.getFirstQueryStringParameter("id");
                            return response()
                                    .withStatusCode(200)
                                    .withBody("{\"id\": \"" + id +"\"}");
                        }
                );


    }

}
