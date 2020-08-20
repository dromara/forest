package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.BodyLifeCycle;

import java.lang.annotation.*;

/**
 * @author gongjun
 * @since 2020-08-10
 */
@Documented
@ParamLifeCycle(BodyLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Body {

    /**
     * URL query name
     * @return
     */
    String value() default "";

    /**
     * The filters will do some processing for the query value before sending request.
     * @return
     */
    String filter() default "";

}
