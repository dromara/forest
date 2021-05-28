package com.dtflys.forest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可组合的Forest参数注解
 *
 * <p>可以在自定义注解中组合其他的Forest参数级别注解</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ComposableParamAnnotation {
}
