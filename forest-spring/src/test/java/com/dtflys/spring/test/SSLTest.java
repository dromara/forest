package com.dtflys.spring.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.ssl.SSLKeyStore;
import com.dtflys.spring.test.client0.BeastshopClient;
import com.dtflys.spring.test.client1.BaiduClient;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 14:40
 */
public class SSLTest extends TestCase {

    private ClassPathXmlApplicationContext applicationContext;

    private BaiduClient baiduClient;

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
        assertEquals("com.dtflys.spring.test.ssl.MySSLSocketFactoryBuilder", keyStore.getSslSocketFactoryBuilder());
        BeastshopClient beastshopClient =
                (BeastshopClient) applicationContext.getBean("beastshopClient");
        assertNotNull(beastshopClient);
        String result = beastshopClient.index();
        assertNotNull(result);
    }

}
