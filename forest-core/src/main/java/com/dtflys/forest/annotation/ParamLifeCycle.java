package com.dtflys.forest.annotation;

import com.dtflys.forest.reflection.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ParameterAnnotationLifeCycle;

import java.lang.annotation.*;

/**
 * Life Cycle Class Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ParamLifeCycle {
    Class<? extends ParameterAnnotationLifeCycle> value();
}
