package org.dromara.forest.solon.test;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.logging.ForestLogger;
import org.dromara.forest.solon.test.client2.GiteeClient;
import org.dromara.forest.solon.test.ssl.MySSLSocketFactoryBuilder;
import org.dromara.forest.ssl.SSLKeyStore;
import org.mockito.Mockito;
import org.noear.solon.annotation.Inject;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;


//@RunWith(SolonJUnit4ClassRunner.class)
//@SolonTest(env = "ssl")
@Deprecated
public class SSLTest {

    @Inject
    private ForestConfiguration sslConfig;

    @Inject
    private GiteeClient giteeClient;


//    @Test
    public void testConfiguration() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);

        assertEquals(Integer.valueOf(300), sslConfig.getMaxConnections());
        assertEquals(Integer.valueOf(300), sslConfig.getMaxRouteConnections());
        assertEquals(Integer.valueOf(3000), sslConfig.getTimeout());
        assertEquals(Integer.valueOf(3000), sslConfig.getConnectTimeout());
        assertEquals(Integer.valueOf(2), sslConfig.getMaxRetryCount());
        assertEquals(1, sslConfig.getSslKeyStores().size());
        SSLKeyStore sslKeyStore = sslConfig.getKeyStore("keystore1");
        assertThat(sslKeyStore).isNotNull();
//        assertEquals("keystore1", sslKeyStore.getId());
//        assertEquals("test.keystore", sslKeyStore.getFilePath());
//        assertEquals("123456", sslKeyStore.getKeystorePass());
//        assertEquals("123456", sslKeyStore.getCertPass());
//        assertEquals(1, sslKeyStore.getProtocols().length);
//        assertEquals("SSLv3", sslKeyStore.getProtocols()[0]);
        assertThat(sslKeyStore.getSslSocketFactoryBuilder()).isNotNull().isInstanceOf(MySSLSocketFactoryBuilder.class);

        ForestRequest<String> request = giteeClient.index2();
        assertThat(request).isNotNull();
        assertThat(request.url().isSSL()).isTrue();
        request.getLogConfiguration().getLogHandler().setLogger(logger);
        String result = (String) request.execute();
        assertThat(result.startsWith("Global: ")).isTrue();
        Mockito.verify(logger).info("[Forest] [Test2] 请求: \n" + "\tGET https://gitee.com/dt_flys HTTPS");
    }

}
