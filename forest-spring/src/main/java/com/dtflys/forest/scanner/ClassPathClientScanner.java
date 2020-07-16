package com.dtflys.forest.scanner;

import com.dtflys.forest.beans.ClientFactoryBean;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.utils.ClientFactoryBeanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 14:46
 */
public class ClassPathClientScanner extends ClassPathBeanDefinitionScanner {

    private final String configurationId;

    private boolean allInterfaces = true;

    public ClassPathClientScanner(String configurationId, BeanDefinitionRegistry registry) {
        super(registry, false);
        this.configurationId = configurationId;
        registerFilters();
    }

    /**
     * 注册过滤器
     */
    public void registerFilters() {
        if (allInterfaces) {
            // include all interfaces
            addIncludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    return true;
                }
            });
        }

        // exclude package-info.java
        addExcludeFilter(new TypeFilter() {
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            }
        });
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition) holder.getBeanDefinition();

            if (logger.isDebugEnabled()) {
                logger.debug("[Forest] Creating Forest Client Bean with name '" + holder.getBeanName()
                        + "' and Proxy of '" + definition.getBeanClassName() + "' client interface");
            }

            String beanClassName = definition.getBeanClassName();
            ClientFactoryBeanUtils.setupClientFactoryBean(definition, configurationId, beanClassName);
            logger.info("[Forest] Created Forest Client Bean with name '" + holder.getBeanName()
                    + "' and Proxy of '" + beanClassName + "' client interface");

        }
    }


    /**
     * 重写扫描逻辑
     * @param basePackages
     * @return
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("[Forest] No Forest client is found in package '" + Arrays.toString(basePackages) + "'.");
        }
        processBeanDefinitions(beanDefinitions);
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
