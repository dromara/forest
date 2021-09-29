package com.dtflys.forest.springboot;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.spring.ForestBeanProcessor;
import com.dtflys.forest.springboot.annotation.ForestScannerRegister;
import com.dtflys.forest.springboot.properties.ForestConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.dtflys.forest.springboot.properties")
@EnableConfigurationProperties({ForestConfigurationProperties.class})
@Import({ForestScannerRegister.class})
public class ForestAutoConfiguration {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Bean
    public ForestBeanProcessor forestBeanProcessor() {
        return new ForestBeanProcessor();
    }

    @Bean
    public ForestBeanRegister forestBeanRegister(ForestConfigurationProperties forestConfigurationProperties) {
        ForestBeanRegister forestBeanRegister = new ForestBeanRegister(applicationContext, forestConfigurationProperties);
        forestBeanRegister.registerForestConfiguration(forestConfigurationProperties);
        forestBeanRegister.registerScanner(forestConfigurationProperties);
        return forestBeanRegister;
    }

}
