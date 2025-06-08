package com.dtflys.forest.test.mock;

import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.Configuration;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.MockServerRule;
import org.mockserver.netty.MockServer;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-17 16:08
 */
public class OAuth2MockServer extends MockServerRule {

    public final static String EXPECTED = "{\"status\": \"ok\"}";

    public final static String TOKEN = "65_LkGUa1odrH9zbjWPm72XOwrfNso123s_S6HXds2wrTiQ0kOfptOEsEG-bkVNbke6gRpoATSlZMvRpoE4RCwAOOdAFkLtfd8Xx0eINHJK6rdUO-4Oy2Lc6E6SFNHHc-3j3bdQlsqVKNYsvVNVGIfAJAYJL";


    public final static String TOKEN_JSON = "{" +
            "\"access_token\": \"" + TOKEN + "\"," +
            "\"expires_in\": \"1\"" +
            "}";
    public final static String DEFINITION_TOKEN_JSON = "{\"token\":\"" + TOKEN + "\"}";
    
    private MockServerClient server;



    public OAuth2MockServer(Object target) {
        super(target);
    }

    public void initServer() {
        if (server != null) {
            return;
        }
        Configuration configuration = new Configuration();
        configuration.maxSocketTimeoutInMillis(100000L);
        server = new MockServerClient(configuration, "localhost", getPort());
        server.when(
                request()
                        .withPath("/auth/oauth/token")
                        .withMethod("POST")
                        .withBody("client_id=password&client_secret=123456&scope=any&grant_type=password&username=root&password=123456")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(TOKEN_JSON)
        );


        server.when(
                request()
                        .withPath("/auth/test/password")
                        .withMethod("GET")
                        .withHeader("Authorization",
                                "Bearer " + TOKEN)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );

        server.when(
                request()
                        .withPath("/auth/test/password_at_url")
                        .withMethod("GET")
                        .withQueryStringParameter("access_token", TOKEN)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );

        server.when(
                request()
                        .withPath("/auth/oauth/client_credentials/token")
                        .withMethod("POST")
                        .withBody("client_id=client_credentials&client_secret=123456&scope=any&grant_type=client_credentials")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(TOKEN_JSON)
        );

        server.when(
                request()
                        .withPath("/auth/test/client_credentials")
                        .withMethod("GET")
                        .withHeader("Authorization",
                                "Bearer " + TOKEN)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );

        server.when(
                request()
                        .withPath("/auth/test/client_credentials_at_url")
                        .withMethod("GET")
                        .withQueryStringParameter("access_token", TOKEN)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(EXPECTED)
        );
        server.when(
                request()
                        .withPath("/auth/oauth/token/definition")
                        .withMethod("POST")
                        .withBody("client_id=password&client_secret=123456&scope=any&grant_type=password&username=root&password=123456")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(DEFINITION_TOKEN_JSON)
        );

    }
    
    public void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
            

}
