package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.WeiXinOAuth2Client;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HouKunLin
 * @date 2020/11/23 0023 21:36
 */
public class TestWeiXinOAuth2Client extends BaseClientTest {
    private final static Logger log = LoggerFactory.getLogger(TestWeiXinOAuth2Client.class);

    private static ForestConfiguration configuration;

    private static WeiXinOAuth2Client weiXinOAuth2Client;

    public TestWeiXinOAuth2Client(HttpBackend backend) {
        super(backend, configuration);
        weiXinOAuth2Client = configuration.createInstance(WeiXinOAuth2Client.class);
    }

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

    @Test
    public void testListMenu() {
        System.out.println(weiXinOAuth2Client.listMenu());
    }

    @Test
    public void testListCustomMenu() {
        System.out.println(weiXinOAuth2Client.listCustomMenu());
    }

    @Test
    public void testGetCurrentAutoreplyInfo() {
        System.out.println(weiXinOAuth2Client.getCurrentAutoreplyInfo());
    }
}
