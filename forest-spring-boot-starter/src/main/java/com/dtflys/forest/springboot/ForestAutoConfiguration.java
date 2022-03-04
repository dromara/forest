package com.dtflys.forest.springboot;

import com.dtflys.forest.spring.ForestBeanProcessor;
import com.dtflys.forest.springboot.annotation.ForestScannerRegister;
import com.dtflys.forest.springboot.properties.ForestConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

import javax.annotation.Resource;

@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ComponentScan("com.dtflys.forest.springboot.properties")
@EnableConfigurationProperties({ForestConfigurationProperties.class})
@Import({ForestScannerRegister.class})
public class ForestAutoConfiguration {

    @Autowired(required = false)
    private ConfigurableApplicationContext applicationContext;


    @Bean
    @ConditionalOnMissingBean
    public ForestBeanProcessor forestBeanProcessor() {
        return new ForestBeanProcessor();
    }


    @Bean
    @ConditionalOnMissingBean
    public ForestBeanRegister getForestBeanRegister(ForestConfigurationProperties forestConfigurationProperties) {
        ForestBeanRegister register = new ForestBeanRegister(applicationContext, forestConfigurationProperties);
        register.registerForestConfiguration(forestConfigurationProperties);
        register.registerScanner(forestConfigurationProperties);
        return register;
    }

}
