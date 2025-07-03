package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.parameter.CookieLifeCycle;

import java.lang.annotation.*;

/**
 * Cookie 参数注解
 *
 * @since 1.7.4
 */
@Documented
@ParamLifeCycle(CookieLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Cookie {

    /**
     * Cookie 名称（可选参数）[同name]
     * @return Cookie 名称
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Cookie 名称（可选参数）[同value]
     * @return Cookie 名称
     */
    @AliasFor("value")
    String name() default "";

}
