package com.dtflys.forest.annotation;

import com.dtflys.forest.callback.RetryWhen;
import com.dtflys.forest.lifecycles.method.RetryLifeCycle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求重试注解
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
@Documented
@MethodLifeCycle(RetryLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Retry {

    /**
     * 请求最大重试次数
     */
    String maxRetryCount() default "";

    /**
     *
     * @return
     */
    String maxRetryInterval() default "";

    /**
     * 请求重试触发条件
     */
    Class<? extends RetryWhen> condition() default RetryWhen.class;

}
