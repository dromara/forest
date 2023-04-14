package org.dromara.forest.test.http;

import com.alibaba.fastjson.JSON;
import org.dromara.forest.Forest;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.converter.json.ForestJacksonConverter;
import org.dromara.forest.test.http.client.AmapClient;
import org.dromara.forest.test.model.Coordinate;
import org.dromara.forest.test.model.Location;
import org.dromara.forest.test.model.Result;
import org.dromara.forest.test.model.SubCoordinate;

import java.util.Map;

import static junit.framework.Assert.assertNotNull;

public class TestAmapClient {

    private static ForestConfiguration configuration;

    private static AmapClient amapClient;


//    @BeforeClass
    public static void prepareClient() {
        configuration = Forest.config();
        configuration.setJsonConverter(new ForestJacksonConverter());
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

//    @Test
    public void testGetLocation3() {
        Result<Location> result = amapClient.getLocationWithJavaObject(new Coordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }

/*
    @Test
    public void testGetLocation4() {
        Result<AmapLocation<AmapCross>> result = amapClient.getLocationWithJavaObject2(new Coordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }
*/

/*
    @Test
    public void testGetLocation5() {
        ForestResponse<Result<AmapLocation<AmapLocation.AmapCross>>> response =
                amapClient.getLocationWithJavaObject3(
                        new Coordinate("121.475078", "31.223577"));
        assertNotNull(response);
        Result<AmapLocation<AmapLocation.AmapCross>> result = response.getResult();
        assertNotNull(result);
        assertTrue(result instanceof Result);
        AmapLocation<AmapLocation.AmapCross> location = result.getData();
        assertNotNull(location);
        assertTrue(location instanceof AmapLocation);
        List<AmapLocation.AmapCross> crossList = location.getCross_list();
        assertNotNull(crossList);
        assertTrue(crossList.get(0) instanceof AmapLocation.AmapCross);
        System.out.println(JSON.toJSONString(result));
    }
*/

//    @Test
    public void testGetLocation6() {
        Map result = amapClient.getLocation(new SubCoordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }

//    @Test
    public void testGetLocationWithDecoder() {
        Map result = amapClient.getLocationWithDecoder(new SubCoordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }

//    @Test
    public void testGetLocationWithDecoder2() {
        Map result = amapClient.getLocationWithDecoder2(new SubCoordinate("121.475078", "31.223577"));
        assertNotNull(result);
        System.out.println(JSON.toJSONString(result));
    }


}
