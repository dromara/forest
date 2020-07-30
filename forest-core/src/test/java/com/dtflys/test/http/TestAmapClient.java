package com.dtflys.test.http;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.test.http.client.AmapClient;
import com.dtflys.test.model.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class TestAmapClient {

    private static ForestConfiguration configuration;

    private static AmapClient amapClient;


    @BeforeClass
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

    @Test
    public void testGetLocation2() {
        Map result = amapClient.getLocation(new Coordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void testGetLocation3() {
        Result<Location> result = amapClient.getLocationWithJavaObject(new Coordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void testGetLocation4() {
        Result<AmapLocation<AmapCross>> result = amapClient.getLocationWithJavaObject2(new Coordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void testGetLocation5() {
        ForestResponse<Result<AmapLocation<AmapCross>>> response =
                amapClient.getLocationWithJavaObject3(
                        new Coordinate("121.475078", "31.223577"));
        assertNotNull(response);
        Result<AmapLocation<AmapCross>> result = response.getResult();
        assertNotNull(result);
        assertTrue(result instanceof Result);
        AmapLocation<AmapCross> location = result.getData();
        assertNotNull(location);
        assertTrue(location instanceof AmapLocation);
        List<AmapCross> crossList = location.getCross_list();
        assertNotNull(crossList);
        assertTrue(crossList.get(0) instanceof AmapCross);
        System.out.println(JSON.toJSONString(result));
    }

}
