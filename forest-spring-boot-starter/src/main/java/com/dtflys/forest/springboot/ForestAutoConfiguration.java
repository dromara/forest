package com.dtflys.forest.springboot;

import com.dtflys.forest.config.SpringForestProperties;
import com.dtflys.forest.interceptor.SpringInterceptorFactory;
import com.dtflys.forest.reflection.SpringForestObjectFactory;
import com.dtflys.forest.spring.ForestBeanProcessor;
import com.dtflys.forest.springboot.annotation.ForestScannerRegister;
import com.dtflys.forest.springboot.properties.ForestConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

@AutoConfiguration
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
    public ForestBeanRegister forestBeanRegister(ConfigurableApplicationContext applicationContext,SpringForestProperties properties,
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
