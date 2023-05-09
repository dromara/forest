package org.dromara.forest.core.test.http;

import org.dromara.forest.backend.HttpBackend;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.core.test.http.client.OAuth2Client;
import org.dromara.forest.core.test.mock.OAuth2MockServer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        configuration.setVariableValue("port", server.getPort());
        oAuth2Client = configuration.createInstance(OAuth2Client.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    @Override
    public void afterRequests() {
    }

    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testPassword() {
        String result = oAuth2Client.testPassword();
        assertThat(result).isNotNull();
        assertThat(result).isNotNull().isEqualTo(OAuth2MockServer.EXPECTED);
    }

    @Test
    public void testPasswordTokenAtURL() {
        String result = oAuth2Client.testPasswordTokenAtURL("root", "123456");
        assertThat(result).isNotNull();
        assertThat(result).isNotNull().isEqualTo(OAuth2MockServer.EXPECTED);
    }

    @Test
    public void testClientCredentials() {
        String result = oAuth2Client.testClientCredentials();
        assertThat(result).isNotNull();
        assertThat(result).isNotNull().isEqualTo(OAuth2MockServer.EXPECTED);
    }

    @Test
    public void testClientCredentialsTokenAtURL() {
        String result = oAuth2Client.testClientCredentialsTokenAtURL();
        assertThat(result).isNotNull();
        assertThat(result).isNotNull().isEqualTo(OAuth2MockServer.EXPECTED);

    }


    @Test
    public void testDefinitionOAuth2() {
        String result = oAuth2Client.testDefinitionOAuth2();
        assertThat(result).isNotNull();
        assertThat(result).isNotNull().isEqualTo(OAuth2MockServer.EXPECTED);
    }

}
