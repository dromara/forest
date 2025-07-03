package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.AutoCookieSaveAndLoadLifeCycle;

import java.lang.annotation.*;

/**
 * Cookie 自动存取注解
 *
 * @since 1.7.4
 */
@Documented
@MethodLifeCycle(AutoCookieSaveAndLoadLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@RequestAttributes
public @interface AutoCookieSaveAndLoad {

    /**
     * 是否允许该请求的 Cookie 自动存取, 默认为允许
     * @return {@code true}: 允许自动存取, {@code false}: 不允许
     */
    boolean value() default true;

}
