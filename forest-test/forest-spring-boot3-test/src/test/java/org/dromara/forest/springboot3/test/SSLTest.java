package org.dromara.forest.springboot3.test;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.logging.ForestLogger;
import org.dromara.forest.springboot3.test.client2.GiteeClient;
import org.dromara.forest.springboot3.test.ssl.MySSLSocketFactoryBuilder;
import org.dromara.forest.ssl.SSLKeyStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@RunWith(SpringRunner.class)
@ActiveProfiles("ssl")
@SpringBootTest(classes = SSLTest.class)
@EnableAutoConfiguration
public class SSLTest {

    @Resource
    private ForestConfiguration sslConfig;

    @Resource
    private GiteeClient giteeClient;


    @Test
    public void testConfiguration() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);

        assertThat(sslConfig.getMaxConnections()).isEqualTo(300);
        assertThat(sslConfig.getMaxRouteConnections()).isEqualTo(300);
        assertThat(sslConfig.getTimeout()).isEqualTo(3000);
        assertThat(sslConfig.getConnectTimeout()).isEqualTo(3000);
        assertThat(sslConfig.getMaxRetryCount()).isEqualTo(2);
        assertThat(sslConfig.getSslKeyStores().size()).isEqualTo(1);

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
        String result = request.executeAsString();
        assertThat(result.startsWith("Global: ")).isTrue();
        Mockito.verify(logger).info("[Forest] [Test2] 请求: \n" + "\tGET https://gitee.com/dt_flys HTTPS");
    }

}
