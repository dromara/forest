package org.dromara.forest.springboot;

import org.dromara.forest.config.SpringForestProperties;
import org.dromara.forest.interceptor.SpringInterceptorFactory;
import org.dromara.forest.reflection.SpringForestObjectFactory;
import org.dromara.forest.spring.ForestBeanProcessor;
import org.dromara.forest.springboot.annotation.ForestScannerRegister;
import org.dromara.forest.springboot.properties.ForestConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({ForestConfigurationProperties.class})
@Import({ForestScannerRegister.class})
public class ForestAutoConfiguration {

    @Resource
    private ConfigurableApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public SpringForestProperties forestProperties() {
        return new SpringForestProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringForestObjectFactory forestObjectFactory() {
        return new SpringForestObjectFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringInterceptorFactory forestInterceptorFactory() {
        return new SpringInterceptorFactory();
    }


    @Bean
    @DependsOn("forestBeanProcessor")
    @ConditionalOnMissingBean
    public ForestBeanRegister forestBeanRegister(SpringForestProperties properties,
                                                    SpringForestObjectFactory forestObjectFactory,
                                                    SpringInterceptorFactory forestInterceptorFactory,
                                                    ForestConfigurationProperties forestConfigurationProperties) {
        ForestBeanRegister register = new ForestBeanRegister(
                applicationContext,
                forestConfigurationProperties,
                properties,
                forestObjectFactory,
                forestInterceptorFactory);
        register.registerForestConfiguration();
        register.registerScanner();
        return register;
    }

    @Bean
    @ConditionalOnMissingBean
    public ForestBeanProcessor forestBeanProcessor() {
        return new ForestBeanProcessor();
    }

}
