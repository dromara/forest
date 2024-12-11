package com.dtflys.forest.test.jsonpath;

import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.test.model.TestUser;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class TestJSONPath {

    public final static String SINGLE_EXPECTED = "{\"status\":\"ok\", \"data\": {\"name\": \"Foo\", \"age\": 12}}";

    public final static String LIST_EXPECTED = "{\"status\":\"ok\", \"data\": [{\"name\": \"Foo\", \"age\": 12}, {\"name\": \"Bar\", \"age\": 22}]}";

    @Rule
    public final MockWebServer server = new MockWebServer();

    private static ForestConfiguration configuration;

    private final TestJSONPathClient testJSONPathClient;

    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.createConfiguration();
    }

    public TestJSONPath() {
        configuration.setVariableValue("port", server.getPort());
        this.testJSONPathClient = configuration.client(TestJSONPathClient.class);
    }

    @Test
    public void testGetSingleUser() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(SINGLE_EXPECTED));
        TestUser user = testJSONPathClient.getSingleUser();
        System.out.println(JSONObject.toJSONString(user));
    }

    @Test
    public void testGetListOfUser() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(LIST_EXPECTED));
        List<TestUser> userList = testJSONPathClient.getListOfUsers();
        System.out.println(JSONObject.toJSONString(userList));
    }

    @Test
    public void getListOfUserAges() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(LIST_EXPECTED));
        List<Integer> ages = testJSONPathClient.getListOfUserAges();
        System.out.println(JSONObject.toJSONString(ages));
    }

    @Test
    public void getListOfUserAgesWithVariable() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody(LIST_EXPECTED));
        List<Integer> ages = testJSONPathClient.getListOfUserAges(20);
        System.out.println(JSONObject.toJSONString(ages));
    }


}
