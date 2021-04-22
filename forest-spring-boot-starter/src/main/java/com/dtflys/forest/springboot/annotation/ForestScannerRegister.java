package com.dtflys.forest.springboot.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author guihuo   (E-mail:1620657419@qq.com)
 * @since 2018-09-25 11:59
 */
public class ForestScannerRegister implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {


    private ResourceLoader resourceLoader;

    public static List<String> basePackages = new ArrayList<>();

    public static String configurationId;

    private BeanFactory beanFactory;


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(ForestScan.class.getName());
        if (annotationAttributes == null) {
            basePackages.clear();
            basePackages.addAll(AutoConfigurationPackages.get(beanFactory));
            return;
        }

        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationAttributes);


        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }

        for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        configurationId = annoAttrs.getString("configuration");



//        ClassPathClientScanner scanner = new ClassPathClientScanner(configurationId, registry);
//        // this check is needed in Spring 3.1
//        if (resourceLoader != null) {
//            scanner.setResourceLoader(resourceLoader);
//        }
//
//        scanner.registerFilters();
//        scanner.doScan(StringUtils.toStringArray(basePackages));

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
