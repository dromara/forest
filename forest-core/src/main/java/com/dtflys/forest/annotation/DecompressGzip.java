package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.DecompressGzipLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记是否开启解压GZIP响应内容的注解
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.1
 */
@Documented
@MethodLifeCycle(DecompressGzipLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DecompressGzip {

    /**
     * 否开启解压GZIP响应内容的注解
     * <p>{@code true}为开启，否则为不开启</p>
     */
    boolean value() default true;

}
