package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.logging.BaseLogEnabledLifeCycle;
import com.dtflys.forest.lifecycles.logging.LogEnabledLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forest请求日志控制注解
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-10-13 16:22
 */
@Documented
@BaseLifeCycle(BaseLogEnabledLifeCycle.class)
@MethodLifeCycle(LogEnabledLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface LogEnabled {

    /**
     * 是否打印请求/响应日志
     */
    boolean value() default true;

    /**
     * 是否打印请求日志
     */
    boolean logRequest() default true;

    /**
     * 是否打印响应状态日志
     */
    boolean logResponseStatus() default true;

    /**
     * 是否打印响应内容日志
     */
    boolean logResponseContent() default false;

}
