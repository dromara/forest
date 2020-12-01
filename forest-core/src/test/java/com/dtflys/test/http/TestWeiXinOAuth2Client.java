package com.dtflys.test.http;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.WeiXinOAuth2Client;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

/**
 * @author HouKunLin
 */
public class TestWeiXinOAuth2Client /*extends BaseClientTest*/ {
    private final static Logger log = LoggerFactory.getLogger(TestWeiXinOAuth2Client.class);

    private static ForestConfiguration configuration;

    private static WeiXinOAuth2Client weiXinOAuth2Client;

    public TestWeiXinOAuth2Client(HttpBackend backend) {
//        super(backend, configuration);
        weiXinOAuth2Client = configuration.createInstance(WeiXinOAuth2Client.class);
    }

//    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
    }

//    @Test
    public void testListMenu() {
        Map<String, Object> result = weiXinOAuth2Client.listMenu();
        System.out.println(JSON.toJSONString(result));
        assertNotNull(result);
    }

//    @Test
    public void testListCustomMenu() {
        Map<String, Object> result = weiXinOAuth2Client.listCustomMenu();
        System.out.println(JSON.toJSONString(result));
        assertNotNull(result);
    }

//    @Test
    public void testGetCurrentAutoreplyInfo() {
        Map<String, Object> result = weiXinOAuth2Client.getCurrentAutoreplyInfo();
        System.out.println(JSON.toJSONString(result));
        assertNotNull(result);
    }
}
