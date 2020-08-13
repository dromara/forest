package com.dtflys.forest.annotation;

import com.dtflys.forest.reflection.MetaLifeCycle;

import java.lang.annotation.*;

/**
 * Interceptor Class Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface LifeCycle {
    Class<? extends MetaLifeCycle> value();
}
