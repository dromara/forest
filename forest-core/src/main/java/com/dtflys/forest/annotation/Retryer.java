package com.dtflys.forest.annotation;

import com.dtflys.forest.lifecycles.method.RetryLifeCycle;
import com.dtflys.forest.lifecycles.method.RetryerLifeCycle;
import com.dtflys.forest.retryer.ForestRetryer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求重试器注解
 *
 * @author gongjun [dt_flys@hotmail.com]
 */
@Documented
@MethodLifeCycle(RetryerLifeCycle.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Retryer {

    /**
     * 所要的配置的 Forest 请求重试器类
     */
    Class<? extends ForestRetryer> value();
}
