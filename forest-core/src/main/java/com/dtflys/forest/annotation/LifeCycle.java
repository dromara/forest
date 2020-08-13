package com.dtflys.forest.annotation;

import com.dtflys.forest.reflection.MetaRequestLifeCycle;

import java.lang.annotation.*;

/**
 * Interceptor Class Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface LifeCycle {
    Class<? extends MetaRequestLifeCycle> value();
}
