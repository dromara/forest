package org.dromara.forest.spring.test;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.spring.test.ssl.MySSLSocketFactoryBuilder;
import org.dromara.forest.ssl.SSLKeyStore;
import org.dromara.forest.spring.test.client0.BeastshopClient;
import org.dromara.forest.spring.test.client1.BaiduClient;
import org.dromara.forest.spring.test.ssl.MyHostnameVerifier;
import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        assertThat(keyStore.getSslSocketFactoryBuilder()).isNotNull().isInstanceOf(MySSLSocketFactoryBuilder.class);
        assertThat(keyStore.getHostnameVerifier()).isNotNull().isInstanceOf(MyHostnameVerifier.class);
        BeastshopClient beastshopClient =
                (BeastshopClient) applicationContext.getBean("beastshopClient");
        assertNotNull(beastshopClient);
        String result = beastshopClient.index();
        assertNotNull(result);

        Throwable th = null;
        try {
            beastshopClient.index2();
        } catch (ForestRuntimeException ex) {
            th = ex.getCause();
        }
        assertThat(th).isNotNull();

    }

}
