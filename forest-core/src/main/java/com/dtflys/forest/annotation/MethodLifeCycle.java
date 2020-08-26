package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Life Cycle Class Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MethodLifeCycle {
    Class<? extends MethodAnnotationLifeCycle> value();
}
