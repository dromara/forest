package com.dtflys.forest.springboot.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.springboot.test.client2.GiteeClient;
import com.dtflys.forest.ssl.SSLKeyStore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;

import static org.junit.Assert.*;

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

        assertEquals(Integer.valueOf(300), sslConfig.getMaxConnections());
        assertEquals(Integer.valueOf(300), sslConfig.getMaxRouteConnections());
        assertEquals(Integer.valueOf(3000), sslConfig.getTimeout());
        assertEquals(Integer.valueOf(3000), sslConfig.getConnectTimeout());
        assertEquals(Integer.valueOf(2), sslConfig.getRetryCount());
        assertEquals(1, sslConfig.getSslKeyStores().size());
        SSLKeyStore sslKeyStore = sslConfig.getKeyStore("keystore1");
        assertNotNull(sslKeyStore);
        assertEquals("keystore1", sslKeyStore.getId());
        assertEquals("test.keystore", sslKeyStore.getFilePath());
        assertEquals("123456", sslKeyStore.getKeystorePass());
        assertEquals("123456", sslKeyStore.getCertPass());
//        assertEquals(1, sslKeyStore.getProtocols().length);
//        assertEquals("SSLv3", sslKeyStore.getProtocols()[0]);
        assertEquals("com.dtflys.forest.springboot.test.ssl.MySSLSocketFactoryBuilder", sslKeyStore.getSslSocketFactoryBuilder());

        ForestRequest<String> request = giteeClient.index2();
        assertNotNull(request);
        request.getLogConfiguration().getLogHandler().setLogger(logger);
        String result = (String) request.execute();
        assertTrue(result.startsWith("Global: "));
        Mockito.verify(logger).info("[Forest] [SSLTest] 请求: \n" + "\tGET https://gitee.com/dt_flys HTTPS");
    }

}
