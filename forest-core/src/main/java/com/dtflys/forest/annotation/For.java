package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.BodyLifeCycle;
import com.dtflys.forest.lifecycles.parameter.ForLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@ParamLifeCycle(ForLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface For {

    String value() default "";

    String index() default "";
}
