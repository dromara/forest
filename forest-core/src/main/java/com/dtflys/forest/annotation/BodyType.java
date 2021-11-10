package com.dtflys.forest.annotation;

import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.lifecycles.method.DeleteRequestLifeCycle;
import com.dtflys.forest.lifecycles.parameter.BodyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@MethodLifeCycle(DeleteRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface BodyType {

    @AliasFor("type")
    String value() default "";

    @AliasFor("value")
    String type() default "";

    Class<? extends ForestEncoder> encoder() default ForestEncoder.class;
}
