package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.OAuth2Client;
import com.dtflys.test.mock.OAuth2MockServer;
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
    public OAuth2MockServer server = new OAuth2MockServer(this);

    private static ForestConfiguration configuration;

    private static OAuth2Client oAuth2Client;

    public TestOAuth2Client(HttpBackend backend) {
        super(backend, configuration);
        oAuth2Client = configuration.createInstance(OAuth2Client.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", OAuth2MockServer.port);
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testPassword() {
        String result = oAuth2Client.testPassword();
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);
    }

    @Test
    public void testPasswordTokenAtURL() {
        String result = oAuth2Client.testPasswordTokenAtURL("root", "123456");
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);
    }

    @Test
    public void testClientCredentials() {
        String result = oAuth2Client.testClientCredentials();
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);
    }

    @Test
    public void testClientCredentialsTokenAtURL() {
        String result = oAuth2Client.testClientCredentialsTokenAtURL();
        assertNotNull(result);
        assertEquals(OAuth2MockServer.EXPECTED, result);

    }

}
