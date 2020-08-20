package com.dtflys.forest.annotation;

import com.dtflys.forest.reflection.MethodAnnotationLifeCycle;

import java.lang.annotation.*;

/**
 * Life Cycle Class Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MethodLifeCycle {
    Class<? extends MethodAnnotationLifeCycle> value();
}
