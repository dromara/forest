package com.thebeastshop.forest.springboot;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.interceptor.SpringInterceptorFactory;
import com.dtflys.forest.schema.ForestConfigurationBeanDefinitionParser;
import com.dtflys.forest.utils.StringUtils;
import com.thebeastshop.forest.springboot.properties.ForestConfigurationProperties;
import com.thebeastshop.forest.springboot.properties.ForestSSLKeyStoreProperties;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

public class ForestBeanRegister {

    private final ConfigurableApplicationContext applicationContext;

    public ForestBeanRegister(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ForestConfiguration registerForestConfiguration(ForestConfigurationProperties forestConfigurationProperties) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ForestConfiguration.class);
        String id = forestConfigurationProperties.getBeanId();
        if (StringUtils.isBlank(id)) {
            id = "forestConfiguration";
        }
        beanDefinitionBuilder
                .addPropertyValue("maxConnections", forestConfigurationProperties.getMaxConnections())
                .addPropertyValue("maxRouteConnections", forestConfigurationProperties.getMaxRouteConnections())
                .addPropertyValue("timeout", forestConfigurationProperties.getTimeout())
                .addPropertyValue("connectTimeout", forestConfigurationProperties.getConnectTimeout())
                .addPropertyValue("retryCount", forestConfigurationProperties.getRetryCount())
                .addPropertyValue("backendName", forestConfigurationProperties.getBackend())
                .addPropertyValue("interceptors", forestConfigurationProperties.getInterceptors())
                .addPropertyValue("sslProtocol", forestConfigurationProperties.getSslProtocol())
                .addPropertyValue("variables", forestConfigurationProperties.getVariables())
                .setLazyInit(false)
                .setFactoryMethod("configuration");

        BeanDefinition interceptorFactoryBeanDefinition = registerInterceptorFactoryBean();
        beanDefinitionBuilder.addPropertyValue("interceptorFactory", interceptorFactoryBeanDefinition);

        List<ForestSSLKeyStoreProperties> sslKeyStorePropertiesList = forestConfigurationProperties.getSslKeyStores();
        ManagedMap<String, BeanDefinition> sslKeystoreMap = new ManagedMap<>();
        for (ForestSSLKeyStoreProperties keyStoreProperties : sslKeyStorePropertiesList) {
            registerSSLKeyStoreBean(sslKeystoreMap, keyStoreProperties);
        }

        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinition.getPropertyValues().addPropertyValue("sslKeyStores", sslKeystoreMap);

        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition(id, beanDefinition);

        ForestConfiguration configuration = applicationContext.getBean(id, ForestConfiguration.class);
        return configuration;
    }

    public BeanDefinition registerInterceptorFactoryBean() {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringInterceptorFactory.class);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition("forestInterceptorFactory", beanDefinition);
        return beanDefinition;
    }

    public BeanDefinition registerSSLKeyStoreBean(ManagedMap<String, BeanDefinition> map, ForestSSLKeyStoreProperties sslKeyStoreProperties) {
        String id = sslKeyStoreProperties.getId();
        if (StringUtils.isBlank(id)) {
            throw new ForestRuntimeException("[Forest] Property 'id' of SSL keystore can not be empty or blank");
        }
        if (map.containsKey(id)) {
            throw new ForestRuntimeException("[Forest] Duplicate SSL keystore id '" + id + "'");
        }

        BeanDefinition beanDefinition = ForestConfigurationBeanDefinitionParser.createSSLKeyStoreBean(
                id,
                sslKeyStoreProperties.getType(),
                sslKeyStoreProperties.getFile(),
                sslKeyStoreProperties.getKeystorePass(),
                sslKeyStoreProperties.getCertPass(),
                sslKeyStoreProperties.getProtocols(),
                sslKeyStoreProperties.getCipherSuites()
        );
        map.put(id, beanDefinition);
        return beanDefinition;
    }

}
