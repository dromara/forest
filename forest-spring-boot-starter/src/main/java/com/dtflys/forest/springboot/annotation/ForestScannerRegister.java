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
public class ForestScannerRegister implements BeanFactoryAware, ImportBeanDefinitionRegistrar {

    /**
     * 所要扫描的所有包名
     */
    private static List<String> basePackages = new ArrayList<>();

    /**
     * Forest全局配置ID
     */
    private static String configurationId;

    private BeanFactory beanFactory;


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(ForestScan.class.getName());
        // 先扫描 @ForestScan 注解中定义的包
        if (annotationAttributes != null) {
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
        }

        // 若 @ForestScan 注解未定义扫描包名，则扫描整个项目
        if (basePackages.isEmpty()) {
            basePackages.addAll(AutoConfigurationPackages.get(beanFactory));
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 获取所要扫描的所有包名
     *
     * @return 包名字符串列表
     */
    public static List<String> getBasePackages() {
        return basePackages;
    }

    /**
     * 获取Forest全局配置ID
     *
     * @return Forest全局配置ID字符串
     */
    public static String getConfigurationId() {
        return configurationId;
    }
}
