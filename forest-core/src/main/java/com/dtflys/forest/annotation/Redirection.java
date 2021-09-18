package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.RedirectionLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定重定向注解
 * <p>用于指定请求在遇到302、307等状态码时是否会自动进行重定向
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2021-09-18 21:20
 */
@Documented
@MethodLifeCycle(RedirectionLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Redirection {

    /**
     * 是否打开自动重定向
     * <p>默认为 true，即打开自动重定向
     *
     * @return {@code true}: 打开, {@code false}: 禁止
     */
    boolean value() default true;

}
