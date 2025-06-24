package com.dtflys.forest.test.http;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.test.http.client.OAuth2Client;
import com.dtflys.forest.test.mock.OAuth2MockServer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author HouKunLin
 */
public class TestOAuth2Client extends BaseClientTest {

    @Rule
    public MockWebServer server = new MockWebServer();

    
//    @Rule
//    public OAuth2MockServer server2 = new OAuth2MockServer(this);

    private static ForestConfiguration configuration;

    private static OAuth2Client oAuth2Client;

    public TestOAuth2Client(String backend, String jsonConverter) {
        super(backend, jsonConverter, configuration);
        configuration.setVariableValue("port", method -> server.getPort());
        oAuth2Client = configuration.createInstance(OAuth2Client.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }
    
    @Override
    public void afterRequests() {
    }

/*
    @Before
    public void setup() {
        server.initServer();
    }
*/

    @Test
    public void testPassword() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.TOKEN_JSON));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.EXPECTED));
        String result = oAuth2Client.testPassword();
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);
    }

    @Test
    public void testPasswordTokenAtURL() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.TOKEN_JSON));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.EXPECTED));
        String result = oAuth2Client.testPasswordTokenAtURL("root", "123456");
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);
    }

    @Test
    public void testClientCredentials() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.TOKEN_JSON));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.EXPECTED));
        String result = oAuth2Client.testClientCredentials();
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);
    }

    @Test
    public void testClientCredentialsTokenAtURL() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.TOKEN_JSON));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.EXPECTED));
        String result = oAuth2Client.testClientCredentialsTokenAtURL();
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);

    }


    @Test
    public void testDefinitionOAuth2() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.DEFINITION_TOKEN_JSON));
        server.enqueue(new MockResponse().setResponseCode(200).setBody(OAuth2MockServer.EXPECTED));
        String result = oAuth2Client.testDefinitionOAuth2();
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);
    }

}
