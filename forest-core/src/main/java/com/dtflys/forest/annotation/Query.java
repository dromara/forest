package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.QueryLifeCycle;

import java.lang.annotation.*;

/**
 * @author gongjun
 * @since 2020-08-03
 */
@Documented
@ParamLifeCycle(QueryLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Query {
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
