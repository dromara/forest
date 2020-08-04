package com.dtflys.forest.annotation;

import java.lang.annotation.*;

/**
 * Interceptor Class Annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface InterceptorClass {
    Class value();
}
