package com.dtflys.forest.scanner;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.file.SpringResource;
import com.dtflys.forest.http.ForestRequestBody;
import com.dtflys.forest.http.body.MultipartRequestBodyBuilder;
import com.dtflys.forest.http.body.RequestBodyBuilder;
import com.dtflys.forest.http.body.ResourceRequestBodyBuilder;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.utils.ClientFactoryBeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
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

    private Boolean forestAnnotation;

    private final String FOREST_ANNOTATION = "forestAnnotation";

    public ClassPathClientScanner(String configurationId, BeanDefinitionRegistry registry) {
        super(registry, false);
        this.configurationId = configurationId;
        registerFilters();
        registerMultipartTypes();
        BeanDefinition forestConfiguration = registry.getBeanDefinition(ForestConfiguration.class.getName());
        forestAnnotation = (Boolean) forestConfiguration.getPropertyValues().getPropertyValue(FOREST_ANNOTATION).getValue();
    }

    /**
     * 注册能上传下载的文件类型
     */
    public void registerMultipartTypes() {
        ForestMultipartFactory.registerFactory(Resource.class, SpringResource.class);
        RequestBodyBuilder.registerBodyBuilder(Resource.class, new ResourceRequestBodyBuilder());
        try {
            Class multipartFileClass = Class.forName("org.springframework.web.multipart.MultipartFile");
            Class springMultipartFileClass = Class.forName("com.dtflys.forest.file.SpringMultipartFile");
            ForestMultipartFactory.registerFactory(multipartFileClass, springMultipartFileClass);
            RequestBodyBuilder.registerBodyBuilder(multipartFileClass, new MultipartRequestBodyBuilder());
        } catch (ClassNotFoundException e) {
        }
    }


    /**
     * 注册过滤器
     */
    public void registerFilters() {
        if (this.forestAnnotation) {
            addIncludeFilter(new TypeFilter() {
                @Override
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    Set<String> annotationTypes = metadataReader.getAnnotationMetadata().getAnnotationTypes();
                    return annotationTypes.contains("com.dtflys.forest.springboot.annotation.Forest");
                }
            });
        } else {
            if (allInterfaces) {
                // include all interfaces
                addIncludeFilter(new TypeFilter() {
                    @Override
                    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                        return true;
                    }
                });
            }
        }
        // exclude package-info.java
        addExcludeFilter(new TypeFilter() {
            @Override
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
     * @param basePackages 请求接口类所在的包路径，只能是第一层的包，不包含子包
     * @return BeanDefinitionHolder实例集合
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
