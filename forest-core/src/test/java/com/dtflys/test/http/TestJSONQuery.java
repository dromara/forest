package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.mock.JSONQueryMockServer;
import com.dtflys.test.mock.RepeatableQueryMockServer;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestJSONQuery extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetTokenClient.class);

    @Rule
    public JSONQueryMockServer server = new JSONQueryMockServer(this);

    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", JSONQueryMockServer.port);
    }


    public TestJSONQuery(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testJSONQuery() {
        List<Integer> idList = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        Map<String, String> userInfo = new LinkedHashMap<>();
        userInfo.put("username", "foo");
        userInfo.put("password", "bar");
        String result = getClient.jsonQuery(idList, userInfo);
        assertNotNull(result);
        assertEquals(RepeatableQueryMockServer.EXPECTED, result);
    }


}
