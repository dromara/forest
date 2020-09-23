package com.dtflys.forest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解可以修饰其他自定义注解，被修饰过的自定义注解中的所有属性都会被自定转换为请求对象的属性
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-04 19:16
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RequestAttributes {
}
