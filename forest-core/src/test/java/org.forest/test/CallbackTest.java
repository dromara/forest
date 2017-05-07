package org.forest.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.client.CallbackClient;
import org.forest.config.ForestConfiguration;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.callback.OnSuccess;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author gongjun
 * @since 2016-05-31
 */
public class CallbackTest extends TestCase {
    private static Log log = LogFactory.getLog(CallbackTest.class);

    public void testCallback() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        CallbackClient callbackClient = configuration.createInstance(CallbackClient.class);
        BigDecimal longitude = new BigDecimal("121.04925573429551");
        BigDecimal latitude = new BigDecimal("31.315590522490712");
        String text = callbackClient.testOnSuccess(longitude, latitude, new OnSuccess<Map>() {
            public void onSuccess(Map data, ForestRequest request, ForestResponse response) {
                log.info(data);
                assertNotNull(data);
                assertEquals(data.get("status"), "1");
            }
        });
        log.info(text);
        assertNotNull(text);
    }

}
