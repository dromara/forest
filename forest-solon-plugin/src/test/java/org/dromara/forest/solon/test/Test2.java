package org.dromara.forest.solon.test;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.logging.ForestLogger;
import org.dromara.forest.solon.test.client2.GiteeClient;
import org.dromara.forest.solon.test.interceptor.GlobalInterceptor;
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
        assertEquals(Integer.valueOf(5000), forestConfiguration.getConnectTimeout());
        assertEquals(Integer.valueOf(6000), forestConfiguration.getReadTimeout());
        assertEquals(Integer.valueOf(0), forestConfiguration.getMaxRetryCount());
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
                "\tGET https://gitee.com/dt_flys HTTPS");
    }


}
