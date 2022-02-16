package com.dtflys.test.mock;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Parameter;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class SSLMockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static int port = 60896;

    public SSLMockServer(Object target) {
        super(target, port);
    }

    public void initServer() {
        ClientAndServer clientAndServer = new ClientAndServer("localhost", port);
        clientAndServer.when(
                        request()
                                .withSecure(true)
                                .withMethod("GET")
                                .withPath("/con")
                                .withQueryStringParameter(new Parameter("id", "1"))
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody(EXPECTED)
                );
/*
                .respond(
                        httpRequest -> {
                            String id = httpRequest.getFirstQueryStringParameter("id");
                            return response()
                                    .withStatusCode(200)
                                    .withBody("{\"id\": \"" + id +"\"}");
                        }
                );
*/


    }

}
