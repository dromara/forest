package org.forest.test.http;

import org.forest.config.ForestConfiguration;
import org.forest.test.http.client.DataClient;
import org.forest.test.mock.DataMockServer;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-06-06 16:12
 */
public class TestDataClient {

    private final static Logger log = LoggerFactory.getLogger(TestDataClient.class);

    @Rule
    public DataMockServer server = new DataMockServer(this);

    private static ForestConfiguration configuration;

    private static DataClient dataClient;


    @BeforeClass
    public static void prepareClient() {
        configuration = ForestConfiguration.configuration();
        dataClient = configuration.createInstance(DataClient.class);
    }


    @Before
    public void prepareMockServer() {
        server.initServer();
    }


//    @Ignore
    @Test
    public void testData() {
        Map<String, Object> dataResult = dataClient.getData("data");
        assertNotNull(dataResult);
        assertEquals(Integer.valueOf(1), dataResult.get("status"));
        Map<String, Object> data = (Map<String, Object>) dataResult.get("data");
        assertNotNull(data);
        List list = (List) data.get("list");
        assertNotNull(list);
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(3), list.get(2));
        assertEquals(Integer.valueOf(4), list.get(3));
        assertEquals(Integer.valueOf(32), list.get(6));
        Map<String, Object> map = (Map<String, Object>) data.get("map");
        assertNotNull(map);
        assertEquals(Integer.valueOf(1), map.get("a"));
        assertEquals(Integer.valueOf(2), map.get("b"));
    }


}
