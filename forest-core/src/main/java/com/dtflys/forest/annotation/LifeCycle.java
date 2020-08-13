package com.dtflys.forest.annotation;

import com.dtflys.forest.reflection.AnnotationLifeCycle;

import java.lang.annotation.*;

/**
 * Life Cycle Class Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface LifeCycle {
    Class<? extends AnnotationLifeCycle> value();
}
