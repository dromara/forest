package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.proxy.HTTPProxyLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP正向代理注解
 * @author gongjun [dt_flys@hotmail.com]
 * @since 1.5.0-BETA5
 */
@Documented
@MethodLifeCycle(HTTPProxyLifeCycle.class)
@RequestAttributes
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface HTTPProxy {

    String host();

    String port() default "80";

}
