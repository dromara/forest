package com.dtflys.forest.springcloud;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author changjin wei(魏昌进)
 * @since 1.5.33
 */
@AutoConfigureAfter({BlockingLoadBalancerClientAutoConfiguration.class, LoadBalancerAutoConfiguration.class})
@ConditionalOnBean({LoadBalancerClient.class, LoadBalancerClientFactory.class})
@Configuration(proxyBeanMethods = false)
@Import(ForestLoadBalancerConfiguration.class)
public class ForestLoadBalancerAutoConfiguration {

}
