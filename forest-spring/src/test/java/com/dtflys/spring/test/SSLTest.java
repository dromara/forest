package com.dtflys.spring.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.ssl.SSLKeyStore;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 14:40
 */
public class SSLTest extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;

    public void testSSL() {
        applicationContext = new ClassPathXmlApplicationContext(
                new String[] { "classpath:ssl-test.xml" });
        ForestConfiguration configuration =
                (ForestConfiguration) applicationContext.getBean("forestConfiguration");
        SSLKeyStore keyStore = configuration.getKeyStore("keystore1");
        assertNotNull(keyStore);
        assertNotNull(keyStore.getInputStream());
        assertEquals("keystore1", keyStore.getId());
        assertEquals("123456", keyStore.getKeystorePass());
        assertEquals("jks", keyStore.getKeystoreType());
    }

}
