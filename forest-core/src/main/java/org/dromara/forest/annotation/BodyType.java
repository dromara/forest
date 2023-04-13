package org.dromara.forest.annotation;

import org.dromara.forest.converter.ForestEncoder;
import org.dromara.forest.lifecycles.method.BodyTypeLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@MethodLifeCycle(BodyTypeLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface BodyType {

    @AliasFor("type")
    String value() default "";

    @AliasFor("value")
    String type() default "";

    Class<? extends ForestEncoder> encoder() default ForestEncoder.class;
}
