package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.URLEncodeLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface URLEncode {

    @AliasFor("charset")
    String value() default "";

    @AliasFor("value")
    String charset() default "";

    boolean enabled() default true;
}
