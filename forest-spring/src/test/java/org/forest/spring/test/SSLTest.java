package org.forest.spring.test;

import junit.framework.TestCase;
import org.forest.config.ForestConfiguration;
import org.forest.spring.test.client.BeastshopClient;
import org.forest.ssl.SSLKeyStore;
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
        assertEquals("21929331", keyStore.getKeystorePass());
        assertEquals("jks", keyStore.getKeystoreType());
        String content = keyStore.getFileContent();
        assertEquals("this is a test keystore file.", content);
    }

}
