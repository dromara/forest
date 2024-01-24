package com.dtflys.forest.solon.test;

import com.dtflys.forest.Forest;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.solon.test.client2.GiteeClient;
import com.dtflys.forest.solon.test.interceptor.GlobalInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(env = "test2")
public class Test2 {

    @Inject
    private GiteeClient giteeClient;

    @Inject
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfiguration() {
        assertEquals("okhttp3", forestConfiguration.getBackendName());
        assertEquals(Integer.valueOf(6000), forestConfiguration.getMaxConnections());
        assertEquals(Integer.valueOf(6600), forestConfiguration.getMaxRouteConnections());
        assertEquals(Integer.valueOf(6000), forestConfiguration.getTimeout());
        assertEquals(Integer.valueOf(5000), forestConfiguration.connectTimeout());
        assertEquals(Integer.valueOf(6000), forestConfiguration.readTimeout());
        assertEquals(Integer.valueOf(0), forestConfiguration.maxRetryCount());
        assertThat(forestConfiguration.isLogEnabled()).isTrue();
        assert forestConfiguration.getInterceptors().size() > 1;
        assertEquals(GlobalInterceptor.class, forestConfiguration.getInterceptors().get(0));
    }


    @Test
    public void testClient2() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        ForestRequest<String> request = giteeClient.index2();
        assertThat(request).isNotNull();
        request.getLogConfiguration().getLogHandler().setLogger(logger);
        String result = (String) request.execute();
        assertThat(result.startsWith("Global: ")).isTrue();
        Mockito.verify(logger).info("[Forest] [Test2] 请求: \n" +
                "\tGET https://gitee.com/dt_flys HTTPS\n" +
                "\t请求头: \n" +
                "\t\tUser-Agent: forest/" + Forest.VERSION);
    }


}
