package org.dromara.forest.spring.test;

import org.dromara.forest.converter.json.ForestGsonConverter;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.dromara.forest.retryer.NoneRetryer;
import org.dromara.forest.spring.test.logging.TestLogHandler;
import junit.framework.TestCase;
import org.dromara.forest.config.ForestConfiguration;
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
        assertEquals(forestConfiguration.getConnectTimeout(), new Integer(10000));
        assertEquals(forestConfiguration.getMaxConnections(), new Integer(500));
        assertEquals(forestConfiguration.getMaxRouteConnections(), new Integer(500));
        assertEquals(forestConfiguration.getSslProtocol(), "SSLv3");
        assertEquals(forestConfiguration.getVariableValue("baseUrl"), "http://www.thebeastshop.com");
        assertEquals(forestConfiguration.getVariableValue("x"), "0");
        assertEquals(forestConfiguration.getVariableValue("y"), "1");
        assertEquals(forestConfiguration.getBackend().getName(), "httpclient");
        assertEquals("GBK", forestConfiguration.getCharset());
        assertEquals(Boolean.TRUE, Boolean.valueOf(forestConfiguration.isLogEnabled()));
        assertEquals(Boolean.FALSE, Boolean.valueOf(forestConfiguration.isLogResponseStatus()));
        assertEquals(Boolean.TRUE, Boolean.valueOf(forestConfiguration.isLogResponseContent()));
        assertEquals(NoneRetryer.class, forestConfiguration.getRetryer());
        assertTrue(forestConfiguration.getLogHandler() instanceof TestLogHandler);
        ForestJsonConverter jsonConverter = forestConfiguration.getJsonConverter();
        assertNotNull(jsonConverter);
        assertTrue(jsonConverter instanceof ForestGsonConverter);
        assertEquals("yyyy/MM/dd hh:mm:ss", jsonConverter.getDateFormat());
    }

}
