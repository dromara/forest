package com.dtflys.test.http;

import com.dtflys.forest.backend.HttpBackend;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.test.http.client.GetClient;
import com.dtflys.test.http.client.GetTokenClient;
import com.dtflys.test.mock.GetMockServer;
import com.dtflys.test.mock.GetTokenMockServer;
import com.dtflys.test.mock.RepeatableQueryMockServer;
import com.dtflys.test.model.TokenResult;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestRepeatableQuery extends BaseClientTest {

    private final static Logger log = LoggerFactory.getLogger(TestGetTokenClient.class);

    @Rule
    public RepeatableQueryMockServer server = new RepeatableQueryMockServer(this);

    private static ForestConfiguration configuration;

    private GetClient getClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("port", RepeatableQueryMockServer.port);
    }


    public TestRepeatableQuery(HttpBackend backend) {
        super(backend, configuration);
        getClient = configuration.createInstance(GetClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }

    @Test
    public void testRepeatableQuery1() {
        String result = getClient.repeatableQuery();
        assertNotNull(result);
        assertEquals(RepeatableQueryMockServer.EXPECTED, result);
    }

    @Test
    public void testRepeatableQuery2() {
        String result = getClient.repeatableQuery("user1", "user2", "123456");
        assertNotNull(result);
        assertEquals(RepeatableQueryMockServer.EXPECTED, result);
    }

    @Test
    public void testRepeatableQuery3() {
        List<String> usernames = Lists.newArrayList("foo", "bar", "user1", "user2");
        String result = getClient.repeatableQuery(usernames, "123456");
        assertNotNull(result);
        assertEquals(RepeatableQueryMockServer.EXPECTED, result);
    }

}
