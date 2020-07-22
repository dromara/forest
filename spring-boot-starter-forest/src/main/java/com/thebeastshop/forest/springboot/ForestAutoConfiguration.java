package com.thebeastshop.forest.springboot;

import com.thebeastshop.forest.springboot.properties.ForestConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.thebeastshop.forest.springboot.properties")
@EnableConfigurationProperties({ForestConfigurationProperties.class})
public class ForestAutoConfiguration {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Bean
    public ForestBeanRegister forestBeanRegister(ForestConfigurationProperties forestConfigurationProperties) {
        ForestBeanRegister forestBeanRegister = new ForestBeanRegister(applicationContext, forestConfigurationProperties);
        forestBeanRegister.registerForestConfiguration(forestConfigurationProperties);
        forestBeanRegister.registerScanner(forestConfigurationProperties);
        return forestBeanRegister;
    }

}
