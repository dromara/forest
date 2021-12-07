package com.dtflys.forest.springboot;

import com.dtflys.forest.spring.ForestBeanProcessor;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @description:
 * @author: Designer
 * @date : 2021/12/7 23:29
 */
public class ForestBeanProcessorRegister implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{ForestBeanProcessor.class.getName()};
    }

}
