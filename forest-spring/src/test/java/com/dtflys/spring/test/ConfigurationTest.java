package com.dtflys.spring.test;

import com.dtflys.forest.ssl.SSLUtils;
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
                (ForestConfiguration) applicationContext.getBean("forestConfiguration");
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
    }

}
