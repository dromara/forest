package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.HeadRequestLifeCycle;
import com.dtflys.forest.logging.DefaultLogHandler;
import com.dtflys.forest.logging.LogHandler;

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
@MethodLifeCycle(HeadRequestLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
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
     * 是否打印响应日志
     */
    boolean logResponse() default true;

    /**
     * 日志处理器
     */
    Class<? extends LogHandler> logHandler() default DefaultLogHandler.class;
}
