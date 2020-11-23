package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.OAuth2Client;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HouKunLin
 * @date 2020/11/23 0023 21:36
 */
public class TestOAuth2Client extends BaseClientTest {
    private final static Logger log = LoggerFactory.getLogger(TestOAuth2Client.class);

    private static ForestConfiguration configuration;

    private static OAuth2Client oAuth2Client;

    public TestOAuth2Client(HttpBackend backend) {
        super(backend, configuration);
        oAuth2Client = configuration.createInstance(OAuth2Client.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    @Test
    public void testPassword() {
        System.out.println(oAuth2Client.testPassword());
    }

    @Test
    public void testClientCredentials() {
        System.out.println(oAuth2Client.testClientCredentials());
    }
}
