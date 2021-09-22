package com.dtflys.forest.annotation;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-23 0:43
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个注解为 Forest 注解
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ForestAnnotation {

    
}
