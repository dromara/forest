package com.dtflys.spring.test;

import com.dtflys.forest.converter.json.ForestGsonConverter;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.retryer.NoneRetryer;
import com.dtflys.spring.test.logging.TestLogHandler;
import junit.framework.TestCase;
import com.dtflys.forest.config.ForestConfiguration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-21 15:06
 */
public class ConfigurationTest extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;

    public void testConfiguration() {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[] { "classpath:configuration-test.xml" });
        ForestConfiguration forestConfiguration =
                (ForestConfiguration) applicationContext.getBean("forestConfiguration2");
        assertNotNull(forestConfiguration);
        assertNotNull(forestConfiguration.getBackend());
        assertEquals(forestConfiguration.getTimeout(), new Integer(30000));
        assertEquals(forestConfiguration.connectTimeout(), new Integer(10000));
        assertEquals(forestConfiguration.getMaxConnections(), new Integer(500));
        assertEquals(forestConfiguration.getMaxRouteConnections(), new Integer(500));
        assertEquals(forestConfiguration.getSslProtocol(), "SSLv3");
        assertEquals(forestConfiguration.getVar("baseUrl"), "http://www.thebeastshop.com");
        assertEquals(forestConfiguration.getVar("x"), "0");
        assertEquals(forestConfiguration.getVar("y"), "1");
        assertEquals(forestConfiguration.getBackend().getName(), "httpclient");
        assertEquals("GBK", forestConfiguration.charset());
        assertEquals(Boolean.TRUE, Boolean.valueOf(forestConfiguration.isLogEnabled()));
        assertEquals(Boolean.FALSE, Boolean.valueOf(forestConfiguration.isLogResponseStatus()));
        assertEquals(Boolean.TRUE, Boolean.valueOf(forestConfiguration.isLogResponseContent()));
        assertEquals(NoneRetryer.class, forestConfiguration.retryer());
        assertTrue(forestConfiguration.getLogHandler() instanceof TestLogHandler);
        ForestJsonConverter jsonConverter = forestConfiguration.getJsonConverter();
        assertNotNull(jsonConverter);
        assertTrue(jsonConverter instanceof ForestGsonConverter);
        assertEquals("yyyy/MM/dd hh:mm:ss", jsonConverter.getDateFormat());
    }

}
