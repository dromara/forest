package com.dtflys.forest.springboot;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.config.SpringForestProperties;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.interceptor.SpringInterceptorFactory;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.reflection.SpringForestObjectFactory;
import com.dtflys.forest.scanner.ClassPathClientScanner;
import com.dtflys.forest.schema.ForestConfigurationBeanDefinitionParser;
import com.dtflys.forest.spring.ConverterBeanListener;
import com.dtflys.forest.springboot.annotation.ForestScannerRegister;
import com.dtflys.forest.springboot.properties.ForestConfigurationProperties;
import com.dtflys.forest.springboot.properties.ForestConvertProperties;
import com.dtflys.forest.springboot.properties.ForestConverterItemProperties;
import com.dtflys.forest.springboot.properties.ForestSSLKeyStoreProperties;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public class ForestBeanRegister implements ResourceLoaderAware, EnvironmentAware, ImportBeanDefinitionRegistrar {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) throws BeansException {
        BindResult<ForestConfigurationProperties> forest = Binder.get(environment)
                .bind("forest", ForestConfigurationProperties.class);
        if (!forest.isBound()) {
            forest.orElseThrow(() -> new IllegalStateException("forest property bound failed"));
        }
        registerForestConfiguration(forest.get(), registry);
    }

    public ForestConfiguration registerForestConfiguration(ForestConfigurationProperties forestConfigurationProperties, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ForestConfiguration.class);
        String id = forestConfigurationProperties.getBeanId();
        if (StringUtils.isBlank(id)) {
            id = "forestConfiguration";
        }

        Class<? extends ForestLogHandler> logHandlerClass = forestConfigurationProperties.getLogHandler();
        ForestLogHandler logHandler = null;
        if (logHandlerClass != null) {
            try {
                logHandler = logHandlerClass.newInstance();
            } catch (InstantiationException e) {
                throw new ForestRuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }

        beanDefinitionBuilder
                .addPropertyValue("maxConnections", forestConfigurationProperties.getMaxConnections())
                .addPropertyValue("maxRouteConnections", forestConfigurationProperties.getMaxRouteConnections())
                .addPropertyValue("timeout", forestConfigurationProperties.getTimeout())
                .addPropertyValue("connectTimeout", forestConfigurationProperties.getConnectTimeoutMillis())
                .addPropertyValue("readTimeout", forestConfigurationProperties.getReadTimeoutMillis())
                .addPropertyValue("charset", forestConfigurationProperties.getCharset())
                .addPropertyValue("retryer", forestConfigurationProperties.getRetryer())
                .addPropertyValue("maxRetryCount", forestConfigurationProperties.getMaxRetryCount())
                .addPropertyValue("maxRetryInterval", forestConfigurationProperties.getMaxRetryInterval())
                .addPropertyValue("autoRedirection", forestConfigurationProperties.isAutoRedirection())
                .addPropertyValue("logEnabled", forestConfigurationProperties.isLogEnabled())
                .addPropertyValue("logRequest", forestConfigurationProperties.isLogRequest())
                .addPropertyValue("logResponseStatus", forestConfigurationProperties.isLogResponseStatus())
                .addPropertyValue("logResponseContent", forestConfigurationProperties.isLogResponseContent())
                .addPropertyValue("logHandler", logHandler)
                .addPropertyValue("backendName", forestConfigurationProperties.getBackend())
                .addPropertyValue("baseAddressScheme", forestConfigurationProperties.getBaseAddressScheme())
                .addPropertyValue("baseAddressHost", forestConfigurationProperties.getBaseAddressHost())
                .addPropertyValue("baseAddressPort", forestConfigurationProperties.getBaseAddressPort())
                .addPropertyValue("baseAddressSourceClass", forestConfigurationProperties.getBaseAddressSource())
                .addPropertyValue("successWhenClass", forestConfigurationProperties.getSuccessWhen())
                .addPropertyValue("retryWhenClass", forestConfigurationProperties.getRetryWhen())
                .addPropertyValue("interceptors", forestConfigurationProperties.getInterceptors())
                .addPropertyValue("sslProtocol", forestConfigurationProperties.getSslProtocol())
                .addPropertyValue("variables", forestConfigurationProperties.getVariables())
                .setLazyInit(false)
                .setFactoryMethod("configuration");

        BeanDefinition forestPropertiesBean = registerForestPropertiesBean(registry);
        beanDefinitionBuilder.addPropertyValue("properties", forestPropertiesBean);

        BeanDefinition forestObjectFactoryBeanDefinition = registerForestObjectFactoryBean(registry);
        beanDefinitionBuilder.addPropertyValue("forestObjectFactory", forestObjectFactoryBeanDefinition);

        BeanDefinition interceptorFactoryBeanDefinition = registerInterceptorFactoryBean(registry);
        beanDefinitionBuilder.addPropertyValue("interceptorFactory", interceptorFactoryBeanDefinition);

        List<ForestSSLKeyStoreProperties> sslKeyStorePropertiesList = forestConfigurationProperties.getSslKeyStores();
        ManagedMap<String, BeanDefinition> sslKeystoreMap = new ManagedMap<>();
        for (ForestSSLKeyStoreProperties keyStoreProperties : sslKeyStorePropertiesList) {
            registerSSLKeyStoreBean(sslKeystoreMap, keyStoreProperties);
        }

        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinition.getPropertyValues().addPropertyValue("sslKeyStores", sslKeystoreMap);
        registry.registerBeanDefinition(id, beanDefinition);

        ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory) registry : null;
        ForestConfiguration configuration = beanFactory.getBean(id, ForestConfiguration.class);

        Map<String, Class> filters = forestConfigurationProperties.getFilters();
        for (Map.Entry<String, Class> entry : filters.entrySet()) {
            String filterName = entry.getKey();
            Class filterClass = entry.getValue();
            configuration.registerFilter(filterName, filterClass);
        }

        ForestConvertProperties convertProperties = forestConfigurationProperties.getConverters();
        if (convertProperties != null) {
            registerConverter(configuration, ForestDataType.TEXT, convertProperties.getText());
            registerConverter(configuration, ForestDataType.JSON, convertProperties.getJson());
            registerConverter(configuration, ForestDataType.XML, convertProperties.getXml());
            registerConverter(configuration, ForestDataType.BINARY, convertProperties.getBinary());
        }

        registerConverterBeanListener(registry, configuration);
        //beanFactory.getBean("forestConverterBeanListener", ConverterBeanListener.class);

        registerScanner(registry);
        return configuration;
    }

    public void registerConverterBeanListener(BeanDefinitionRegistry beanDefinitionRegistry, ForestConfiguration forestConfiguration) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ConverterBeanListener.class);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinition.getPropertyValues().addPropertyValue("forestConfiguration", forestConfiguration);
        beanDefinitionRegistry.registerBeanDefinition("forestConverterBeanListener", beanDefinition);
    }


    private void registerConverter(ForestConfiguration configuration, ForestDataType dataType, ForestConverterItemProperties converterItemProperties) {
        if (converterItemProperties == null) {
            return;
        }
        Class type = converterItemProperties.getType();
        if (type != null) {
            ForestConverter converter = null;
            try {
                Constructor<?>[] constructors = type.getConstructors();
                for (Constructor<?> constructor : constructors) {
                    Parameter[] params = constructor.getParameters();
                    if (params.length == 0) {
                        converter = (ForestConverter) constructor.newInstance(new Object[0]);
                        break;
                    } else if (params.length == 1 &&
                            ForestConfiguration.class.isAssignableFrom(constructor.getParameterTypes()[0])) {
                        converter = (ForestConverter) constructor.newInstance(new Object[]{constructor});
                        break;
                    }
                }


                Map<String, Object> parameters = converterItemProperties.getParameters();
                PropertyDescriptor[] descriptors = ReflectUtils.getBeanSetters(type);
                for (PropertyDescriptor descriptor : descriptors) {
                    String name = descriptor.getName();
                    Object value = parameters.get(name);
                    if (value == null) {
                        continue;
                    }
                    Method method = descriptor.getWriteMethod();
                    if (method != null) {
                        try {
                            method.invoke(converter, value);
                        } catch (IllegalAccessException e) {
                            throw new ForestRuntimeException("An error occurred during setting the property " + type.getName() + "." + name, e);
                        } catch (InvocationTargetException e) {
                            throw new ForestRuntimeException("An error occurred during setting the property " + type.getName() + "." + name, e);
                        }
                    }
                }
                configuration.getConverterMap().put(dataType, converter);
            } catch (InstantiationException e) {
                throw new ForestRuntimeException("[Forest] Convert type '" + type.getName() + "' cannot be initialized!", e);
            } catch (IllegalAccessException e) {
                throw new ForestRuntimeException("[Forest] Convert type '" + type.getName() + "' cannot be initialized!", e);
            } catch (InvocationTargetException e) {
                throw new ForestRuntimeException("[Forest] Convert type '" + type.getName() + "' cannot be initialized!", e);
            }
        }
    }

    public BeanDefinition registerForestPropertiesBean(BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringForestProperties.class);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition("forestProperties", beanDefinition);
        return beanDefinition;
    }


    public BeanDefinition registerForestObjectFactoryBean(BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringForestObjectFactory.class);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition("forestObjectFactory", beanDefinition);
        return beanDefinition;
    }


    public BeanDefinition registerInterceptorFactoryBean(BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringInterceptorFactory.class);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        beanDefinitionRegistry.registerBeanDefinition("forestInterceptorFactory", beanDefinition);
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
                sslKeyStoreProperties.getCipherSuites(),
                sslKeyStoreProperties.getSslSocketFactoryBuilder()
        );
        map.put(id, beanDefinition);
        return beanDefinition;
    }

    public ClassPathClientScanner registerScanner(BeanDefinitionRegistry registry) {
        List<String> basePackages = ForestScannerRegister.getBasePackages();
        String configurationId = ForestScannerRegister.getConfigurationId();

        ClassPathClientScanner scanner = new ClassPathClientScanner(configurationId, registry);
        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
//        scanner.registerFilters();
        if (basePackages == null || basePackages.size() == 0) {
            return scanner;
        }
        scanner.doScan(org.springframework.util.StringUtils.toStringArray(basePackages));
        return scanner;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
