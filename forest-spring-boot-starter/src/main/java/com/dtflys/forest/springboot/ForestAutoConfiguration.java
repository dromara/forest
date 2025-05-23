package com.dtflys.forest.springboot;

import com.dtflys.forest.config.SpringForestProperties;
import com.dtflys.forest.converter.auto.DefaultAutoConverter;
import com.dtflys.forest.interceptor.SpringInterceptorFactory;
import com.dtflys.forest.reflection.SpringForestObjectFactory;
import com.dtflys.forest.spring.ForestBeanProcessor;
import com.dtflys.forest.springboot.annotation.ForestScannerRegister;
import com.dtflys.forest.springboot.properties.ForestConfigurationProperties;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({ForestConfigurationProperties.class})
@Import({ForestScannerRegister.class})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ForestAutoConfiguration {

    @Resource
    private ConfigurableApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SpringForestProperties forestProperties() {
        return new SpringForestProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SpringForestObjectFactory forestObjectFactory() {
        return new SpringForestObjectFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public SpringInterceptorFactory forestInterceptorFactory() {
        return new SpringInterceptorFactory();
    }


    @Bean
    @DependsOn("forestBeanProcessor")
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
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
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ForestBeanProcessor forestBeanProcessor() {
        return new ForestBeanProcessor();
    }

}
