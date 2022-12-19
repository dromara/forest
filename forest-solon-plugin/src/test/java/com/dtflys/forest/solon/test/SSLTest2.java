package com.dtflys.forest.solon.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.solon.test.client2.GiteeClient;
import com.dtflys.forest.solon.test.ssl.MyHostnameVerifier;
import com.dtflys.forest.solon.test.ssl.MySSLSocketFactoryBuilder;
import com.dtflys.forest.ssl.SSLKeyStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;


@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "ssl2")
public class SSLTest2 {

    @Inject
    private ForestConfiguration sslConfig;

    @Inject
    private GiteeClient giteeClient;


    @Test
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
        assertThat(sslKeyStore.getHostnameVerifier()).isNotNull().isInstanceOf(MyHostnameVerifier.class);
        ForestRequest<String> request = giteeClient.index2();
        assertThat(request).isNotNull();
        request.getLogConfiguration().getLogHandler().setLogger(logger);
        String result = (String) request.execute();
        assertThat(result.startsWith("Global: ")).isTrue();
        Mockito.verify(logger).info("[Forest] [Test2] 请求: \n" + "\tGET https://gitee.com/dt_flys HTTPS");
        Throwable th = null;
        try {
            giteeClient.index3();
        } catch (ForestRuntimeException ex) {
            th = ex.getCause();
        }
        assertThat(th).isNotNull();
    }

}
