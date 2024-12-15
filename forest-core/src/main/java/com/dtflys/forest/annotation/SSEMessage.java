package com.dtflys.forest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SSEMessage {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String valueRegex() default "";

    String valuePrefix() default "";

    String valuePostfix() default "";

}
