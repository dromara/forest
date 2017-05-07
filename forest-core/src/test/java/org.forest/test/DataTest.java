package org.forest.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.client.DataClient;
import org.forest.config.ForestConfiguration;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/12.
 */
public class DataTest extends TestCase {
    private static Log log = LogFactory.getLog(DataTest.class);

    private ForestConfiguration configuration = ForestConfiguration.configuration();
    private DataClient dataClient = configuration.createInstance(DataClient.class);

    public void testData() {
        BigDecimal longitude = new BigDecimal("121.04925573429551");
        BigDecimal latitude = new BigDecimal("31.315590522490712");
        Map<String, Object> result = dataClient.getLocation(longitude, latitude);
        log.info(result);
        assertNotNull(result);
        assertEquals("1", result.get("status"));
    }
}
