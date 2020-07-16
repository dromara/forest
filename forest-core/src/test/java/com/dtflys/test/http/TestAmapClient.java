package com.dtflys.test.http;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.AmapClient;
import com.dtflys.test.model.Coordinate;

import java.util.Map;

import static junit.framework.Assert.assertNotNull;

public class TestAmapClient {

    private static ForestConfiguration configuration;

    private static AmapClient amapClient;


//    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setCacheEnabled(false);
        amapClient = configuration.createInstance(AmapClient.class);
    }


//    @Test
    public void testGetLocation1() {
        Map result = amapClient.getLocation("121.475078", "31.223577");
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }

//    @Test
    public void testGetLocation2() {
        Map result = amapClient.getLocation(new Coordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }


}
