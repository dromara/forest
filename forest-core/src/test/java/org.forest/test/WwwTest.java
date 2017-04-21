package org.forest.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.forest.client.WwwClient;
import org.forest.config.ForestConfiguration;
import org.forest.model.ShortUrlResult;
import org.forest.proxy.ProxyFactory;

import java.util.Map;

/**
 * @author gongjun
 * @since 2016-03-24
 */
public class WwwTest extends TestCase {
    private static Log log = LogFactory.getLog(WwwTest.class);

    private ForestConfiguration configuration = ForestConfiguration.configuration();
    private ProxyFactory<WwwClient> factory = configuration.getProxyFactory(WwwClient.class);
    private WwwClient www = factory.createInstance();

    public void testWww() {
        Map result = www.dwz("https://github.com/mySingleLive");
        log.info(result);
        String shortUrl = (String) result.get("tinyurl");
        log.info(shortUrl);
        assertNotNull(result);
    }

    public void testShortUrl() {

        ShortUrlResult result2 = www.getShortUrl("https://github.com/personal");
        log.info(result2.getStatus());
        log.info(result2.getLongurl());
        log.info(result2.getTinyurl());
        assertNotNull(result2);
        assertEquals("https://github.com/personal", result2.getLongurl());

        ShortUrlResult result = www.getShortUrl("https://github.com/");
        log.info(result.getStatus());
        log.info(result.getLongurl());
        log.info(result.getTinyurl());
        assertNotNull(result);
        assertEquals("https://github.com/", result.getLongurl());

    }



}
