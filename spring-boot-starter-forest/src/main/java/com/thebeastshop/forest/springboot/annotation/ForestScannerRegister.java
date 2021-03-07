package com.thebeastshop.forest.springboot.annotation;

import com.dtflys.forest.scanner.ClassPathClientScanner;
import com.thebeastshop.forest.springboot.ForestAutoConfiguration;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guihuo   (E-mail:1620657419@qq.com)
 * @since 2018-09-25 11:59
 */
public class ForestScannerRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {


    private ResourceLoader resourceLoader;

    public static List<String> basePackages = new ArrayList<>();;

    public static String configurationId;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(ForestScan.class.getName()));

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

        ForestScannerRegister.basePackages = basePackages;

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

}
