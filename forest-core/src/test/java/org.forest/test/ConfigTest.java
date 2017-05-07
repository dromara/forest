package org.forest.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.client.BeastshopClient;
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
        configuration.setVariableValue("baseUrl", "http://search.aol.com");
        WwwClient www = configuration.createInstance(WwwClient.class);
        String result = www.aolSearch("Aol");
        log.info(result);
        assertNotNull(result);
    }
}
