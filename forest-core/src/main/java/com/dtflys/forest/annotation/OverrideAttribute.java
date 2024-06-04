package com.dtflys.forest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重写注解属性
 * <p>使用该注解的注解属性可覆盖父属性的同名注解</p>
 * @version 1.5.33
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OverrideAttribute {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";
}
