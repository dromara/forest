package org.forest.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.client.DataClient;
import org.forest.config.ForestConfiguration;

import java.util.Map;

/**
 * Created by Administrator on 2016/6/12.
 */
public class DataTest extends TestCase {
    private static Log log = LogFactory.getLog(DataTest.class);

    private ForestConfiguration configuration = ForestConfiguration.configuration();
    private DataClient dataClient = configuration.createInstance(DataClient.class);

    public void testData() {
        Map<String, Object> result = dataClient.testData("http://www.baidu.com");
        log.info(result);
        assertNotNull(result);
        assertEquals(0, result.get("status"));
    }
}
