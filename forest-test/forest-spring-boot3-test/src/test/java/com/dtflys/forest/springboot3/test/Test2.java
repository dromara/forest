package com.dtflys.forest.springboot3.test;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.SpringInterceptorFactory;
import com.dtflys.forest.logging.ForestLogger;
import com.dtflys.forest.reflection.SpringForestObjectFactory;
import com.dtflys.forest.springboot3.test.client2.GiteeClient;
import com.dtflys.forest.springboot3.test.interceptor.GlobalInterceptor;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


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
        assertThat(forestConfiguration.connectTimeout()).isEqualTo(5000);
        assertThat(forestConfiguration.readTimeout()).isEqualTo(6000);
        assertThat(forestConfiguration.maxRetryCount()).isEqualTo(0);

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
                "\tGET https://gitee.com/dt_flys HTTPS\n" +
                "\t请求头: \n" +
                "\t\tUser-Agent: forest/dev");
    }


}
