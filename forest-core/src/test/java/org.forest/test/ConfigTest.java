package org.forest.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.client.WwwClient;
import org.forest.config.ForestConfiguration;

import java.util.Map;

/**
 * @author gongjun
 * @since 2016-05-31
 */
public class ConfigTest extends TestCase {
    private static Log log = LogFactory.getLog(ConfigTest.class);

    public void testConfig() {
        ForestConfiguration configuration = ForestConfiguration.configuration();
        configuration.setVariableValue("baseShortUrl", "http://dwz.cn");
        WwwClient www = configuration.createInstance(WwwClient.class);
        Map result = www.testVar("https://github.com/mySingleLive");
        log.info(result);
        assertNotNull(result);
    }
}
