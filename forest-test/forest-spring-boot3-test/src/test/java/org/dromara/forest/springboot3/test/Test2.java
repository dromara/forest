package org.dromara.forest.springboot3.test;

import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.interceptor.SpringInterceptorFactory;
import org.dromara.forest.logging.ForestLogger;
import org.dromara.forest.reflection.SpringForestObjectFactory;
import org.dromara.forest.springboot3.test.client2.GiteeClient;
import org.dromara.forest.springboot3.test.interceptor.GlobalInterceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@ActiveProfiles("test2")
@SpringBootTest(classes = Test2.class)
@ComponentScan(basePackageClasses = GlobalInterceptor.class)
@EnableAutoConfiguration
public class Test2 {

    @Resource
    private GiteeClient giteeClient;

    @Resource
    private ForestConfiguration forestConfiguration;

    @Test
    public void testConfiguration() {
        assertThat(forestConfiguration.getBackendName()).isEqualTo("okhttp3");
        assertThat(forestConfiguration.getMaxConnections()).isEqualTo(6000);
        assertThat(forestConfiguration.getMaxRouteConnections()).isEqualTo(6600);
        assertThat(forestConfiguration.getTimeout()).isEqualTo(6000);
        assertThat(forestConfiguration.getConnectTimeout()).isEqualTo(5000);
        assertThat(forestConfiguration.getReadTimeout()).isEqualTo(6000);
        assertThat(forestConfiguration.getMaxRetryCount()).isEqualTo(0);

        assertThat(forestConfiguration.isLogEnabled()).isTrue();
        assertThat(forestConfiguration.getInterceptorFactory()).isInstanceOf(SpringInterceptorFactory.class);
        assertThat(forestConfiguration.getForestObjectFactory()).isInstanceOf(SpringForestObjectFactory.class);
        assertThat(forestConfiguration.getInterceptors().size()).isEqualTo(1);
        assertThat(forestConfiguration.getInterceptors().get(0)).isEqualTo(GlobalInterceptor.class);
    }


    @Test
    public void testClient2() {
        ForestLogger logger = Mockito.mock(ForestLogger.class);
        ForestRequest<String> request = giteeClient.index2();
        assertThat(request).isNotNull();
        request.getLogConfiguration().getLogHandler().setLogger(logger);
        String result = request.executeAsString();
        assertThat(result.startsWith("Global: ")).isTrue();
        Mockito.verify(logger).info("[Forest] [Test2] 请求: \n" +
                "\tGET https://gitee.com/dt_flys HTTPS");
    }


}
