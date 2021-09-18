package com.dtflys.forest.spring;

import com.dtflys.forest.Forest;
import com.dtflys.forest.annotation.BindingVar;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;

public class ForestBeanProcessor implements BeanPostProcessor {

    private void processBean(Object bean, Class beanClass) {
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            BindingVar annotation = method.getAnnotation(BindingVar.class);
            if (annotation == null) {
                continue;
            }
            String confId = annotation.configuration();
            ForestConfiguration configuration = null;
            if (StringUtils.isNotBlank(confId)) {
                configuration = Forest.config(confId);
            } else {
                configuration = Forest.config();
            }
            String varName = annotation.value();
            SpringVariableValue variableValue = new SpringVariableValue(bean, method);
            configuration.setVariableValue(varName, variableValue);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = bean.getClass();
        processBean(bean, beanClass);
        return bean;
    }

}
