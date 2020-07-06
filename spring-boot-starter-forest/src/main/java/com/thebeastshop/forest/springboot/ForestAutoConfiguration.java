package com.thebeastshop.forest.springboot;

import com.thebeastshop.forest.springboot.properties.ForestConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ForestConfigurationProperties.class})
@ConditionalOnProperty(prefix = "forest", name = "enabled", havingValue = "true")
public class ForestAutoConfiguration {

    private final ForestConfigurationProperties forestConfigurationProperties;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    public ForestAutoConfiguration(ForestConfigurationProperties forestConfigurationProperties) {
        this.forestConfigurationProperties = forestConfigurationProperties;
    }

    @Bean
    public ForestBeanRegister forestBeanRegister() {
        ForestBeanRegister forestBeanRegister = new ForestBeanRegister(applicationContext);
        forestBeanRegister.registerForestConfiguration(forestConfigurationProperties);
        return forestBeanRegister;
    }



}
