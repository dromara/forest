package org.forest.spring;

import junit.framework.TestCase;
import org.forest.config.ForestConfiguration;
import org.forest.spring.client.BeastshopClient;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 14:40
 */
public class ScanTest extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;

    public void testConfiguration() {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[] { "classpath:scan-test.xml" });
        BeastshopClient beastshopClient =
                (BeastshopClient) applicationContext.getBean("beastshopClient");
        assertNotNull(beastshopClient);
    }

}
