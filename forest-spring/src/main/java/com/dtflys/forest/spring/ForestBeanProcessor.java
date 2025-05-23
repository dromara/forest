package com.dtflys.forest.spring;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.annotation.Var;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.utils.NameUtils;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class ForestBeanProcessor implements InstantiationAwareBeanPostProcessor {

    public ForestBeanProcessor() {
    }

    private String bindingBeanVariableName(String name, String defaultName) {
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        return defaultName;
    }

    private String bindingMethodVariableName(String name, Method method) {
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        final String methodName = method.getName();
        if (NameUtils.isGetter(methodName)) {
            return NameUtils.propNameFromGetter(methodName);
        }
        return methodName;
    }


    private void processBean(Object bean, Class<?> beanClass, String beanName) {
        final BindingVar classAnnotation = AnnotationUtils.findAnnotation(beanClass, BindingVar.class);
        // 类级别绑定
        if (classAnnotation != null) {
            final String confId = classAnnotation.configuration();
            ForestConfiguration configuration = null;
            if (StringUtils.isNotBlank(confId)) {
                configuration = Forest.config(confId);
            } else {
                configuration = Forest.config();
            }
            final String varName = bindingBeanVariableName(classAnnotation.value(), beanName);
            configuration.setVariable(varName, bean);
        }

        final Var classVarAnnotation = AnnotationUtils.findAnnotation(beanClass, Var.class);
        // 类级别 Var 绑定
        if (classVarAnnotation != null) {
            ForestConfiguration configuration = Forest.config();
            final String varName = bindingBeanVariableName(classVarAnnotation.value(), beanName);
            configuration.setVariable(varName, bean);
        }


        // 方法级别绑定
        final Method[] methods = beanClass.getDeclaredMethods();
        for (final Method method : methods) {
            String confId = null;
            String name = null;
            final BindingVar annotation = method.getAnnotation(BindingVar.class);
            final Var varAnnotation = AnnotationUtils.findAnnotation(method, Var.class);
            if (annotation == null && varAnnotation == null) {
                continue;
            }
            if (annotation != null) {
                confId = annotation.configuration();
                name = annotation.value();
            } else {
                name = varAnnotation.value();
            }
            ForestConfiguration configuration = null;
            if (StringUtils.isNotBlank(confId)) {
                configuration = Forest.config(confId);
            } else {
                configuration = Forest.config();
            }
            final String varName = bindingMethodVariableName(name, method);
            SpringMethodVariableValue variableValue = new SpringMethodVariableValue(bean, method);
            configuration.setVariable(varName, variableValue);
        }
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        final Class<?> beanClass = bean.getClass();
        processBean(bean, beanClass, beanName);
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
