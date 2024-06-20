package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.HeadRequestLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@MethodLifeCycle(HeadRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface HTTPField {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    int sort() default -1;
}
