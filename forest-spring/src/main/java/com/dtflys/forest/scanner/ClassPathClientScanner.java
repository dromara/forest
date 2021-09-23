package com.dtflys.forest.scanner;

import com.dtflys.forest.annotation.BaseLifeCycle;
import com.dtflys.forest.annotation.MethodLifeCycle;
import com.dtflys.forest.file.SpringResource;
import com.dtflys.forest.http.body.MultipartRequestBodyBuilder;
import com.dtflys.forest.http.body.RequestBodyBuilder;
import com.dtflys.forest.http.body.ResourceRequestBodyBuilder;
import com.dtflys.forest.multipart.ForestMultipartFactory;
import com.dtflys.forest.utils.ClientFactoryBeanUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-24 14:46
 */
public class ClassPathClientScanner extends ClassPathBeanDefinitionScanner {

    private final static String[] FOREST_METHOD_ANNOTATION_NAMES = new String[] {
            "com.dtflys.forest.annotation.Backend",
            "com.dtflys.forest.annotation.Headers",
            "com.dtflys.forest.annotation.Address",
            "com.dtflys.forest.annotation.Request",
            "com.dtflys.forest.annotation.Get",
            "com.dtflys.forest.annotation.GetRequest",
            "com.dtflys.forest.annotation.Post",
            "com.dtflys.forest.annotation.PostRequest",
            "com.dtflys.forest.annotation.Put",
            "com.dtflys.forest.annotation.PutRequest",
            "com.dtflys.forest.annotation.HeadRequest",
            "com.dtflys.forest.annotation.Options",
            "com.dtflys.forest.annotation.OptionsRequest",
            "com.dtflys.forest.annotation.Patch",
            "com.dtflys.forest.annotation.PatchRequest",
            "com.dtflys.forest.annotation.Trace",
            "com.dtflys.forest.annotation.TraceRequest",
    } ;


    private final String configurationId;

    private boolean allInterfaces = true;

    public ClassPathClientScanner(String configurationId, BeanDefinitionRegistry registry) {
        super(registry, false);
        this.configurationId = configurationId;
        registerFilters();
        registerMultipartTypes();
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
        } catch (Throwable th) {
        }
    }

    private boolean interfaceFilter(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        if (!classMetadata.isInterface() || classMetadata.isFinal()) {
            return false;
        }

        String[] superClassNames = metadataReader.getClassMetadata().getInterfaceNames();
        boolean hasSuperForestClient = false;
        for (String superClassName : superClassNames) {
            try {
                MetadataReader superMetaReader = metadataReaderFactory.getMetadataReader(superClassName);
                hasSuperForestClient = interfaceFilter(superMetaReader, metadataReaderFactory);
            } catch (IOException e) {
            }
            if (hasSuperForestClient) {
                return true;
            }
        }

        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        for (String annotationTypeName : annotationMetadata.getAnnotationTypes()) {
            if ("com.dtflys.forest.annotation.ForestClient".equals(annotationTypeName)) {
                return true;
            }
            if ("com.dtflys.forest.annotation.BaseRequest".equals(annotationTypeName)) {
                return true;
            }
        }
        for (String methodAnnName : FOREST_METHOD_ANNOTATION_NAMES) {
            if (annotationMetadata.hasAnnotatedMethods(methodAnnName)) {
                return true;
            }
        }

/*
        Set<String> baseAnnNames = annotationMetadata.getAnnotationTypes();
        for (String annName : baseAnnNames) {
            try {
                Class<?> annType = Class.forName(annName);
                Annotation lcAnn = annType.getAnnotation(BaseLifeCycle.class);
                if (lcAnn != null) {
                    return true;
                }
                lcAnn = annType.getAnnotation(MethodLifeCycle.class);
                if (lcAnn != null) {
                    return true;
                }
            } catch (Throwable ignored) {
            }
        }
*/
        return false;
    }

    /**
     * 注册过滤器
     */
    public void registerFilters() {
        if (allInterfaces) {
            // include all interfaces
            addIncludeFilter((metadataReader, metadataReaderFactory) ->
                    interfaceFilter(metadataReader, metadataReaderFactory));
        }

        // exclude package-info.java
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
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
