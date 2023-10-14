package com.dtflys.forest.springcloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClientsProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author changjin wei(魏昌进)
 * @since 1.5.33
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({LoadBalancerClient.class, LoadBalancerClientFactory.class})
@EnableConfigurationProperties(LoadBalancerClientsProperties.class)
public class ForestLoadBalancerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LoadBalancerLifeCycle loadBalancerLifeCycle(LoadBalancerClientFactory loadBalancerClientFactory, LoadBalancerClient loadBalancerClient) {
        return new LoadBalancerLifeCycle(loadBalancerClientFactory, loadBalancerClient);
    }
}
