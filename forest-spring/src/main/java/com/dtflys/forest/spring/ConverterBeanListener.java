package com.dtflys.forest.spring;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.converter.ForestConverter;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;

import java.util.Map;

/**
 * forest数据类型转换器自动注入
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-03-30
 **/
public class ConverterBeanListener implements ApplicationListener<ApplicationContextEvent> {

    private ForestConfiguration forestConfiguration;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        ForestConfiguration forestConfiguration = this.forestConfiguration;
        if (forestConfiguration == null) {
            try {
                forestConfiguration = applicationContext.getBean(ForestConfiguration.class);
            } catch (Exception ignored) {
                throw new ForestRuntimeException("property forestConfiguration undefined", ignored);
            }
        }
        Map<String, ForestConverter> forestConverterMap = applicationContext.getBeansOfType(ForestConverter.class);
        for (ForestConverter forestConverter : forestConverterMap.values()) {
            forestConfiguration.getConverterMap().put(forestConverter.getDataType(), forestConverter);
        }
    }

    public ForestConfiguration getForestConfiguration() {
        return forestConfiguration;
    }

    public void setForestConfiguration(ForestConfiguration forestConfiguration) {
        this.forestConfiguration = forestConfiguration;
    }
}
